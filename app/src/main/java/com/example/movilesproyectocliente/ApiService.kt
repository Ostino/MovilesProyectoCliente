package com.example.movilesproyectocliente

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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

    data class Restaurant(
        val id: Int,
        val name: String,
        val address: String,
        val latitude: String,
        val longitude: String,
        val logo: String
    )

    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun register(@Body request: RegisterRequest): Call<Unit>

    @GET("restaurants")
    fun getRestaurants(@Header("Authorization") authorization: String): Call<List<Restaurant>>

}