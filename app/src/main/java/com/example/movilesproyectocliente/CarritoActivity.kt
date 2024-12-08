package com.example.movilesproyectocliente

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CarritoActivity : AppCompatActivity() {

    private lateinit var carritoAdapter: CarritoAdapter
    private val gson = Gson()
    private lateinit var apiService: ApiService

    // Variables para almacenar los datos recibidos
    private var restaurantId: Int = 0
    private var address: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // Recibir los datos del Intent
        restaurantId = intent.getIntExtra("restaurant_id", 0)
        address = intent.getStringExtra("address")

        // Log para verificar los valores
        Log.d("CarritoActivity", "Restaurant ID: $restaurantId, Address: $address")

        // Inicializar RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCarrito)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener datos del carrito de SharedPreferences
        val sharedPreferences = getSharedPreferences("carrito_prefs", MODE_PRIVATE)
        val carritoJson = sharedPreferences.getString("carrito", null)

        // Decodificar la lista de carrito
        val carritoList: MutableList<ApiService.Carrito> = try {
            if (carritoJson.isNullOrEmpty()) {
                mutableListOf()
            } else {
                gson.fromJson(carritoJson, object : TypeToken<MutableList<ApiService.Carrito>>() {}.type)
            }
        } catch (e: Exception) {
            Log.e("CarritoActivity", "Error al leer el carrito: ${e.message}")
            mutableListOf()
        }

        if (carritoList.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        }

        // Configurar el adaptador
        carritoAdapter = CarritoAdapter(carritoList) { carrito ->
            eliminarProducto(carrito, carritoList)
        }
        recyclerView.adapter = carritoAdapter

        // Configurar el botón de "Realizar Pedido"
        val btnRealizarPedido = findViewById<Button>(R.id.btnRealizarPedido)
        btnRealizarPedido.setOnClickListener {
            // Confirmar si el usuario está seguro de realizar el pedido
            AlertDialog.Builder(this)
                .setTitle("Confirmar Pedido")
                .setMessage("¿Estás seguro de que deseas realizar el pedido?")
                .setPositiveButton("Sí") { dialog, _ ->
                    realizarPedido(carritoList)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun eliminarProducto(carrito: ApiService.Carrito, carritoList: MutableList<ApiService.Carrito>) {
        // Eliminar producto de la lista
        carritoList.remove(carrito)

        // Actualizar SharedPreferences
        val sharedPreferences = getSharedPreferences("carrito_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val updatedCarritoJson = gson.toJson(carritoList)
        editor.putString("carrito", updatedCarritoJson)
        editor.apply()

        // Actualizar adaptador y mostrar mensaje
        carritoAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show()

        // Log para verificar la lista actualizada
        Log.d("CarritoActivity", "Lista después de eliminar: $carritoList")
    }

    private fun realizarPedido(carritoList: MutableList<ApiService.Carrito>) {
        val total = carritoList.sumOf { it.qty * it.price.toInt() }

        val orderDetails = carritoList.map {
            ApiService.Carrito(
                product_id = it.product_id,
                qty = it.qty,
                price = it.price
            )
        }

        val orderRequest = ApiService.OrderRequest(
            restaurant_id = restaurantId,
            total = total,
            address = address ?: "Dirección no disponible",
            latitude = "100.0",
            longitude = "100.0",
            details = orderDetails
        )

        val sharedPreferences = EncryptedSharedPreferences.create(
            "auth_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val token = sharedPreferences.getString("access_token", null)

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        apiService.createOrder("Bearer $token", orderRequest)
            .enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    runOnUiThread {
                        Toast.makeText(this@CarritoActivity, "Error al realizar el pedido", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        if (response.code() == 201) {  // Pedido exitoso
                            runOnUiThread {
                                Toast.makeText(this@CarritoActivity, "Pedido realizado con éxito", Toast.LENGTH_SHORT).show()
                            }

                            // Limpiar carrito después del pedido
                            clearCart(carritoList)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("CarritoActivity", "Error en la solicitud: Código ${response.code()}, Cuerpo: $errorBody")

                        runOnUiThread {
                            Toast.makeText(this@CarritoActivity, "Error al realizar el pedido: Código ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }
    private fun clearCart(carritoList: MutableList<ApiService.Carrito>) {
        // Limpiar lista local
        carritoList.clear()

        // Actualizar SharedPreferences
        val sharedPreferences = getSharedPreferences("carrito_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("carrito") // Borra la entrada completa
        editor.apply()

        // Actualizar el adaptador
        carritoAdapter.notifyDataSetChanged()

        // Verificar si el carrito está vacío
        Toast.makeText(this, "Carrito limpio", Toast.LENGTH_SHORT).show()
    }



}
