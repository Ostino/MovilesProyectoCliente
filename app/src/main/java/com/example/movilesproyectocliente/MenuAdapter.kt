package com.example.movilesproyectocliente

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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuAdapter(private var menuList: List<ApiService.Product>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private val gson = Gson()

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

            // Cargar la imagen con Glide
            Glide.with(itemView.context)
                .load(product.image)
                .override(400, 400)
                .into(productImage)

            // Configurar el botón de "Añadir al carrito"
            addToCartButton.setOnClickListener {
                addToCart(product)
            }
        }

        private fun addToCart(product: ApiService.Product) {
            val sharedPreferences = itemView.context.getSharedPreferences("carrito_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()

            // Obtén el carrito guardado
            val carritoJson = sharedPreferences.getString("carrito", null)
            val carritoList: MutableList<ApiService.Carrito> = try {
                if (carritoJson.isNullOrEmpty()) {
                    mutableListOf() // Si no hay datos, inicializa una lista vacía
                } else {
                    // Intenta convertir el JSON a una lista de objetos Carrito
                    gson.fromJson(carritoJson, object : TypeToken<MutableList<ApiService.Carrito>>() {}.type)
                }
            } catch (e: Exception) {
                Log.e("Carrito", "Error al leer el carrito: ${e.message}")
                mutableListOf() // Si hay error, reinicia la lista
            }

            // Buscar si ya existe el producto en el carrito
            val existingItem = carritoList.find { it.id == product.id }
            if (existingItem != null) {
                // Si ya existe, incrementa la cantidad
                existingItem.qty += 1
            } else {
                // Si no existe, crea un nuevo objeto Carrito y añádelo a la lista
                carritoList.add(ApiService.Carrito(id = product.id, qty = 1, price = product.price))
            }

            // Guarda la lista actualizada en SharedPreferences
            val updatedCarritoJson = gson.toJson(carritoList)
            editor.putString("carrito", updatedCarritoJson)
            editor.apply()

            // Log para verificar el estado del carrito actualizado
            Log.d("Carrito", "Producto añadido: ${product.id}, Contenido actualizado: $carritoList")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(menuList[position])  // Pasamos el producto a bind
    }

    override fun getItemCount(): Int = menuList.size

    // Método para actualizar la lista de productos
    fun updateMenu(products: List<ApiService.Product>) {
        menuList = products
        Log.d("MenuAdapter", "Products received: $menuList")
        notifyDataSetChanged()
    }
}

