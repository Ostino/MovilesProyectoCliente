package com.example.movilesproyectocliente.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movilesproyectocliente.ApiService
import com.example.movilesproyectocliente.R

class PedidoAdapter(private val pedidos: List<ApiService.Pedido>) :
    RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.bind(pedido)
    }

    override fun getItemCount(): Int = pedidos.size

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtPedidoId: TextView = itemView.findViewById(R.id.txtPedidoId)
        private val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)
        private val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        private val txtDireccion: TextView = itemView.findViewById(R.id.txtDireccion)
        private val txtRestauranteId: TextView = itemView.findViewById(R.id.txtRestauranteId)
        private val txtDetalles: TextView = itemView.findViewById(R.id.txtDetalles)

        fun bind(pedido: ApiService.Pedido) {
            txtPedidoId.text = "ID Pedido: ${pedido.id}"
            txtTotal.text = "Total: ${pedido.total} Bs."
            txtFecha.text = "Fecha: ${pedido.created_at}"
            txtDireccion.text = "Dirección: ${pedido.address}"
            txtRestauranteId.text = "ID Restaurante: ${pedido.restaurant_id}"

            val detallesTexto = pedido.order_details.joinToString(separator = "\n") { detalle ->
                "Producto: ${detalle.product.name}, Cantidad: ${detalle.quantity}, Precio: ${detalle.price} Bs"
            }.ifEmpty { "Sin detalles disponibles" }

            txtDetalles.text = "Detalles:\n$detallesTexto"
        }


    }
}