package com.example.movilesproyectocliente

import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.movilesproyectocliente.databinding.ActivityMenuBinding
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call


class MenuActivity : AppCompatActivity() {

    private lateinit var menuAdapter: MenuAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Obtén el restaurantId desde el Intent (por ejemplo, al hacer clic en un restaurante)
        val restaurantId = intent.getIntExtra("restaurantId", -1)

        if (restaurantId == -1) {
            // Si no se pasó el ID del restaurante, muestra un error
            Toast.makeText(this, "Restaurante no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configura el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewMenu)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa el adaptador
        menuAdapter = MenuAdapter(emptyList()) // Comienza con una lista vacía
        recyclerView.adapter = menuAdapter

        // Obtener el token desde SharedPreferences
        val sharedPreferences = EncryptedSharedPreferences.create(
            "auth_prefs",
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val token = sharedPreferences.getString("access_token", null)

        if (token == null) {
            // Si no hay token, el usuario no está autenticado
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish() // O redirige al login
        } else {
            // Usar el token para cargar el menú del restaurante
            loadRestaurantMenu(token, restaurantId)
        }
    }

    private fun loadRestaurantMenu(token: String, restaurantId: Int) {
        // Mostrar un mensaje de carga
        Toast.makeText(this, "Cargando el menú del restaurante...", Toast.LENGTH_SHORT).show()

        // Realizar la llamada a la API para obtener los detalles del restaurante (incluido el menú)
        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        apiService.getRestaurantDetails("Bearer $token", restaurantId).enqueue(object : Callback<ApiService.RestaurantDetailWithMenu> {
            override fun onResponse(
                call: Call<ApiService.RestaurantDetailWithMenu>,
                response: Response<ApiService.RestaurantDetailWithMenu>
            ) {
                if (response.isSuccessful) {
                    // Si la respuesta es exitosa, actualizamos el RecyclerView con los productos
                    val menu = response.body()?.products ?: emptyList()
                    menuAdapter.updateMenu(menu)
                } else {
                    // Si la respuesta no es exitosa, mostramos un mensaje de error
                    Toast.makeText(this@MenuActivity, "Error al cargar el menú", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiService.RestaurantDetailWithMenu>, t: Throwable) {
                // En caso de error de conexión o cualquier otro tipo de fallo
                Toast.makeText(this@MenuActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}