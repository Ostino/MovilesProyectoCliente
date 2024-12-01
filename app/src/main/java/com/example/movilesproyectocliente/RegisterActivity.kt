package com.example.movilesproyectocliente

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameField: EditText
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var registerButton: Button
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameField = findViewById(R.id.name)
        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        registerButton = findViewById(R.id.register_button)

        apiService = RetrofitClient.getClient().create(ApiService::class.java)

        registerButton.setOnClickListener { register() }
    }

    private fun register() {
        val name = nameField.text.toString()
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        val request = ApiService.RegisterRequest(name, email, password)

        apiService.register(request).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                Log.d("RegisterRequest", "Nombre: $name, Email: $email, Password: $password")

                if (response.isSuccessful) {
                    Log.d("RegisterResponse", "Response: ${response.body()}")
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    finish() // Regresa al LoginActivity
                } else {
                    Log.e("RegisterError", "Response code: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@RegisterActivity, "Error en el registro", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("RegisterError", "Error al registrar usuario", t)
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}