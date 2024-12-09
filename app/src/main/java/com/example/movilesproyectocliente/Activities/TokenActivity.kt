package com.example.movilesproyectocliente.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.movilesproyectocliente.ApiService
import com.example.movilesproyectocliente.R
import com.example.movilesproyectocliente.RetrofitClient
import com.example.movilesproyectocliente.adapters.RestaurantAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)
        recyclerView = findViewById(R.id.recyclerViewRestaurants)
        recyclerView.layoutManager = LinearLayoutManager(this)

        restaurantAdapter = RestaurantAdapter(emptyList()) { restaurant ->
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("restaurantId", restaurant.id) // Pasamos el ID del restaurante a la nueva actividad
            intent.putExtra("address", restaurant.address)
            startActivity(intent)
        }
        recyclerView.adapter = restaurantAdapter

        val sharedPreferences = EncryptedSharedPreferences.create(
            "auth_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val token = sharedPreferences.getString("access_token", null)

        token?.let {
            getRestaurants(it)
        } ?: run {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show()
        }
        val btnVerPedidos = findViewById<Button>(R.id.btnVerPedidos)
        btnVerPedidos.setOnClickListener {
            val intent = Intent(this, PedidoActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onResume() {
        super.onResume()
        clearSharedPreferences("carrito_prefs")
    }
    private fun getRestaurants(token: String) {
        apiService = RetrofitClient.getClient().create(ApiService::class.java)
        apiService.getRestaurants("Bearer $token").enqueue(object :
            Callback<List<ApiService.RestaurantDetails>> {
            override fun onResponse(
                call: Call<List<ApiService.RestaurantDetails>>,
                response: Response<List<ApiService.RestaurantDetails>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        restaurantAdapter.updateRestaurants(it)
                    }
                } else {
                    Toast.makeText(this@TokenActivity, "Error al obtener restaurantes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ApiService.RestaurantDetails>>, t: Throwable) {
                Toast.makeText(this@TokenActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun clearSharedPreferences(prefName: String) {
        val sharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

}
