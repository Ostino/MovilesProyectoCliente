package com.example.movilesproyectocliente

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    data class LoginRequest(val email: String, val password: String)

    data class LoginResponse(val access_token: String)

    data class RegisterRequest(
        val name: String,
        val email: String,
        val password: String,
        val role: Int = 2 // Role fijo para usuarios cliente
    )

    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun register(@Body request: RegisterRequest): Call<Unit>
}