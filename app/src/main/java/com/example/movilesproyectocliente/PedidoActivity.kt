package com.example.movilesproyectocliente

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PedidoActivity : AppCompatActivity() {

    private lateinit var pedidoAdapter: PedidoAdapter
    private val pedidosList = mutableListOf<ApiService.Pedido>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPedidos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        pedidoAdapter = PedidoAdapter(pedidosList)
        recyclerView.adapter = pedidoAdapter

        cargarPedidos()
    }

    private fun cargarPedidos() {
        val sharedPreferences = EncryptedSharedPreferences.create(
            "auth_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val token = sharedPreferences.getString("access_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token de usuario no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        apiService.getOrders("Bearer $token").enqueue(object : Callback<List<ApiService.Pedido>> {
            override fun onResponse(call: Call<List<ApiService.Pedido>>, response: Response<List<ApiService.Pedido>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        pedidosList.clear()
                        pedidosList.addAll(it)
                        pedidoAdapter.notifyDataSetChanged()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("PedidoActivity", "Error al obtener pedidos: $errorBody")
                    Toast.makeText(this@PedidoActivity, "Error al cargar los pedidos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ApiService.Pedido>>, t: Throwable) {
                Log.e("PedidoActivity", "Error en la solicitud: ${t.message}")
                Toast.makeText(this@PedidoActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
}