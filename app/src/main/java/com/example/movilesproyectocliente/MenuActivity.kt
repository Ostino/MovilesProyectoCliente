package com.example.movilesproyectocliente

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call


class MenuActivity : AppCompatActivity() {

    private lateinit var menuAdapter: MenuAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val restaurantId = intent.getIntExtra("restaurantId", -1)
        val address = intent.getStringExtra("address")

        if (restaurantId == -1) {
            Toast.makeText(this, "Restaurante no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerViewMenu)
        recyclerView.layoutManager = LinearLayoutManager(this)

        menuAdapter = MenuAdapter(emptyList())
        recyclerView.adapter = menuAdapter

        val sharedPreferences = EncryptedSharedPreferences.create(
            "auth_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val token = sharedPreferences.getString("access_token", null)

        if (token == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            loadRestaurantMenu(token, restaurantId)
        }

        val viewCartButton = findViewById<Button>(R.id.buttonViewCarrito)
        viewCartButton.setOnClickListener {
            val intent = Intent(this, CarritoActivity::class.java)
            intent.putExtra("restaurant_id", restaurantId)
            intent.putExtra("address", address)
            startActivity(intent)
        }
    }

    private fun loadRestaurantMenu(token: String, restaurantId: Int) {
        Toast.makeText(this, "Cargando el menú del restaurante...", Toast.LENGTH_SHORT).show()

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        apiService.getRestaurantDetails("Bearer $token", restaurantId).enqueue(object : Callback<ApiService.RestaurantDetailWithMenu> {
            override fun onResponse(
                call: Call<ApiService.RestaurantDetailWithMenu>,
                response: Response<ApiService.RestaurantDetailWithMenu>
            ) {
                if (response.isSuccessful) {
                    val menu = response.body()?.products ?: emptyList()
                    menuAdapter.updateMenu(menu)
                } else {
                    Toast.makeText(this@MenuActivity, "Error al cargar el menú", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.RestaurantDetailWithMenu>, t: Throwable) {
                Toast.makeText(this@MenuActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

