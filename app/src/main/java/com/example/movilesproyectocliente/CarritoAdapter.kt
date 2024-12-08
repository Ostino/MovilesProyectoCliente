package com.example.movilesproyectocliente

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarritoAdapter(
    private val carritoList: MutableList<ApiService.Carrito>,
    private val onDelete: (ApiService.Carrito) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.textViewProductNameCarrito)
        val productQty: TextView = itemView.findViewById(R.id.textViewProductQtyCarrito)
        val productPrice: TextView = itemView.findViewById(R.id.textViewProductPriceCarrito)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteCarrito)

        fun bind(carritoItem: ApiService.Carrito) {
            productName.text = "Producto ID: ${carritoItem.product_id}"
            productQty.text = "Cantidad: ${carritoItem.qty}"
            productPrice.text = "Precio: ${carritoItem.price}"

            deleteButton.setOnClickListener {
                onDelete(carritoItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(carritoList[position])
    }

    override fun getItemCount(): Int = carritoList.size
}
