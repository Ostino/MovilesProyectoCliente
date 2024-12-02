package com.example.movilesproyectocliente

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RestaurantAdapter(private var restaurants: List<ApiService.Restaurant>) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    // Método para actualizar los restaurantes en el adaptador
    fun updateRestaurants(newRestaurants: List<ApiService.Restaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged() // Notificamos que los datos han cambiado
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.nameTextView.text = restaurant.name
        holder.addressTextView.text = restaurant.address

        // Cargar la imagen del logo con Glide
        Glide.with(holder.logoImageView.context)
            .load(restaurant.logo)
            .into(holder.logoImageView)
    }

    override fun getItemCount(): Int = restaurants.size

    // Vista de cada restaurante
    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.restaurant_name)
        val addressTextView: TextView = itemView.findViewById(R.id.restaurant_address)
        val logoImageView: ImageView = itemView.findViewById(R.id.restaurant_logo)
    }
}