package com.example.movilesproyectocliente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movilesproyectocliente.ApiService
import com.example.movilesproyectocliente.R

class RestaurantAdapter(
    private var restaurantList: List<ApiService.RestaurantDetails>,
    private val onClick: (ApiService.RestaurantDetails) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.restaurant_name)
        private val address: TextView = itemView.findViewById(R.id.restaurant_address)
        private val logo: ImageView = itemView.findViewById(R.id.restaurant_logo)

        fun bind(restaurant: ApiService.RestaurantDetails) {
            name.text = restaurant.name
            address.text = restaurant.address
            Glide.with(itemView.context).load(restaurant.logo).into(logo)
            itemView.setOnClickListener { onClick(restaurant) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(restaurantList[position])
    }

    override fun getItemCount(): Int = restaurantList.size

    fun updateRestaurants(newRestaurants: List<ApiService.RestaurantDetails>) {
        restaurantList = newRestaurants
        notifyDataSetChanged()
    }
}