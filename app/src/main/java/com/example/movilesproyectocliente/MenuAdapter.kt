package com.example.movilesproyectocliente

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MenuAdapter(private var menuList: List<ApiService.Product>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.textViewProductName)
        val productDescription: TextView = itemView.findViewById(R.id.textViewProductDescription)
        val productPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val productImage: ImageView = itemView.findViewById(R.id.imageViewProductImage)

        // Método para asignar los datos del producto al ViewHolder
        fun bind(product: ApiService.Product) {
            productName.text = product.name
            productDescription.text = product.description
            productPrice.text = product.price
            Log.d("MenuAdapter", "Cargando imagen: ${product.image}")

            // Cargar la imagen con Glide y usar un marcador de posición si no hay imagen
            Glide.with(itemView.context)
                .load(product.image)
                .override(400, 400)// Aquí pasas la URL de la imagen
                .into(productImage)  // Cargar la imagen en el ImageView
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