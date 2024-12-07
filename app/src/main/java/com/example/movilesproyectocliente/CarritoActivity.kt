package com.example.movilesproyectocliente

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CarritoActivity : AppCompatActivity() {

    private lateinit var carritoAdapter: CarritoAdapter
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // Inicializar RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCarrito)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener datos del carrito de SharedPreferences
        val sharedPreferences = getSharedPreferences("carrito_prefs", MODE_PRIVATE)
        val carritoJson = sharedPreferences.getString("carrito", null) // Usa la clave correcta

        // Decodificar la lista de carrito
        val carritoList: MutableList<ApiService.Carrito> = try {
            if (carritoJson.isNullOrEmpty()) {
                mutableListOf() // Si no hay datos, inicializa una lista vacía
            } else {
                gson.fromJson(carritoJson, object : TypeToken<MutableList<ApiService.Carrito>>() {}.type)
            }
        } catch (e: Exception) {
            Log.e("CarritoActivity", "Error al leer el carrito: ${e.message}")
            mutableListOf()
        }

        // Log para verificar el contenido recuperado
        Log.d("CarritoActivity", "Lista recuperada: $carritoList")

        if (carritoList.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
        }

        // Configurar el adaptador
        carritoAdapter = CarritoAdapter(carritoList) { carrito ->
            eliminarProducto(carrito, carritoList)
        }
        recyclerView.adapter = carritoAdapter
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
}