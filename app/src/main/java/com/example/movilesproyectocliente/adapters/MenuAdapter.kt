package com.example.movilesproyectocliente.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movilesproyectocliente.ApiService
import com.example.movilesproyectocliente.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuAdapter(private var menuList: List<ApiService.Product>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.textViewProductName)
        val productDescription: TextView = itemView.findViewById(R.id.textViewProductDescription)
        val productPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val productImage: ImageView = itemView.findViewById(R.id.imageViewProductImage)
        val addToCartButton: Button = itemView.findViewById(R.id.buttonAddToCart)

        fun bind(product: ApiService.Product) {
            productName.text = product.name
            productDescription.text = product.description
            productPrice.text = product.price

            Glide.with(itemView.context)
                .load(product.image)
                .override(400, 400)
                .into(productImage)

            addToCartButton.setOnClickListener {
                addToCart(product)
            }
        }

        private fun addToCart(product: ApiService.Product) {
            val sharedPreferences = itemView.context.getSharedPreferences("carrito_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()

            val carritoJson = sharedPreferences.getString("carrito", null)
            val carritoList: MutableList<ApiService.Carrito> = try {
                if (carritoJson.isNullOrEmpty()) {
                    mutableListOf()
                } else {
                    gson.fromJson(carritoJson, object : TypeToken<MutableList<ApiService.Carrito>>() {}.type)
                }
            } catch (e: Exception) {
                Log.e("Carrito", "Error al leer el carrito: ${e.message}")
                mutableListOf()
            }

            val existingItem = carritoList.find { it.product_id == product.id }
            if (existingItem != null) {
                existingItem.qty += 1
            } else {
                carritoList.add(
                    ApiService.Carrito(
                        product_id = product.id,
                        qty = 1,
                        price = product.price
                    )
                )
            }
            val updatedCarritoJson = gson.toJson(carritoList)
            editor.putString("carrito", updatedCarritoJson)
            editor.apply()

            Log.d("Carrito", "Producto añadido: ${product.id}, Contenido actualizado: $carritoList")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int = menuList.size

    fun updateMenu(products: List<ApiService.Product>) {
        menuList = products
        Log.d("MenuAdapter", "Products received: $menuList")
        notifyDataSetChanged()
    }
}

