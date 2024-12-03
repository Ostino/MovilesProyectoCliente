package com.example.movilesproyectocliente

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    data class LoginRequest(val email: String, val password: String)

    data class LoginResponse(val access_token: String)

    data class RegisterRequest(
        val name: String,
        val email: String,
        val password: String,
        val role: Int = 2 // Role fijo para usuarios cliente
    )

    data class RestaurantDetails(
        val id: Int,
        val name: String,
        val address: String,
        val latitude: String,
        val longitude: String,
        val logo: String,
    )
    data class RestaurantDetailWithMenu(
        val id: Int,
        val name: String,
        val address: String,
        val latitude: String,
        val longitude: String,
        val logo: String,
        val products: List<Product>
    )

    data class Product(
        val id: Int,
        val name: String,
        val description: String,
        val price: String,
        val restaurant_id: Int,
        val image: String
    )


    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun register(@Body request: RegisterRequest): Call<Unit>

    @GET("restaurants")
    fun getRestaurants(@Header("Authorization") authorization: String): Call<List<RestaurantDetails>>

    @GET("restaurants/{id}")
    fun getRestaurantDetails(@Header("Authorization") authorization: String, @Path("id") restaurantId: Int
    ): Call<RestaurantDetailWithMenu>

}