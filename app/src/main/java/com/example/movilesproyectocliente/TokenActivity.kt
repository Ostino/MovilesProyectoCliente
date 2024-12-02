package com.example.movilesproyectocliente

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
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

        // Inicializar el RecyclerView y el adaptador
        recyclerView = findViewById(R.id.recyclerViewRestaurants)
        recyclerView.layoutManager = LinearLayoutManager(this)
        restaurantAdapter = RestaurantAdapter(emptyList())  // Inicializamos con una lista vacía
        recyclerView.adapter = restaurantAdapter

        // Obtener el token
        val sharedPreferences = EncryptedSharedPreferences.create(
            "auth_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val token = sharedPreferences.getString("access_token", null)

        // Verificar si tenemos un token y hacer la solicitud
        token?.let {
            getRestaurants(it)
        } ?: run {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRestaurants(token: String) {
        apiService = RetrofitClient.getClient().create(ApiService::class.java)
        apiService.getRestaurants("Bearer $token").enqueue(object :
            Callback<List<ApiService.Restaurant>> {
            override fun onResponse(
                call: Call<List<ApiService.Restaurant>>,
                response: Response<List<ApiService.Restaurant>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        restaurantAdapter.updateRestaurants(it)
                    }
                } else {
                    Toast.makeText(this@TokenActivity, "Error al obtener restaurantes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ApiService.Restaurant>>, t: Throwable) {
                Toast.makeText(this@TokenActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}