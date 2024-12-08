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
        val role: Int = 1 // Role fijo para usuarios cliente
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

    data class Carrito(
        val product_id: Int,
        var qty: Int,
        val price:String
    )
    data class OrderRequest(
        val restaurant_id: Int,
        val total: Int,
        val address: String,
        val latitude: String,
        val longitude: String,
        val details: List<Carrito>
    )

    data class Pedido(
        val id: Int,
        val user_id: Int,
        val restaurant_id: Int,
        val total: String,
        val latitude: String,
        val longitude: String,
        val address: String,
        val driver_id: Int?,
        val status: String,
        val created_at: String,
        val delivery_proof: String,
        val order_details: List<Detalle> // Asegúrate de que el nombre coincida con la respuesta de la API
    )
    data class Detalle(
        val id: Int,
        val quantity: Int,
        val price: String,
        val product: Product
    )

    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun register(@Body request: RegisterRequest): Call<Unit>

    @GET("restaurants")
    fun getRestaurants(@Header("Authorization") authorization: String
    ): Call<List<RestaurantDetails>>

    @GET("restaurants/{id}")
    fun getRestaurantDetails(
        @Header("Authorization") authorization: String,
        @Path("id") restaurantId: Int
    ): Call<RestaurantDetailWithMenu>

    @POST("orders")
    fun createOrder(
        @Header("Authorization") authorization: String,
        @Body orderRequest: OrderRequest
    ): Call<Void>

    @GET("orders")
    fun getOrders(
        @Header("Authorization") authorization: String
    ): Call<List<Pedido>>
}