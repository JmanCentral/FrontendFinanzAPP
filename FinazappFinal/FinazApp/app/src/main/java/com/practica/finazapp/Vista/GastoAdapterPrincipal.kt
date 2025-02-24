package com.practica.finazapp.Vista

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.R
import java.text.NumberFormat


class GastoAdapterPrincipal(private val gastos: List<GastoDTO>) :
    RecyclerView.Adapter<GastoAdapterPrincipal.GastoViewHolder>() {

    private var listener2: OnItemClickListener2? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listelement, parent, false)
        return GastoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {

        holder.cv.setAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_transition))

        val currentGasto = gastos[position]
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        holder.nameTextView.text = currentGasto.nombre_gasto
        holder.valorTextView.text = "${numberFormat.format(currentGasto.valor)}$"
        holder.categoria.text = currentGasto.categoria
        holder.fecha.text = currentGasto.fecha

        // Lógica para seleccionar la imagen según la categoría
        val imagenResId = when (currentGasto.categoria) {
            "Alimentos" -> R.drawable.alimentacion
            "Transporte" -> R.drawable.transporte
            "Servicios" -> R.drawable.servicios
            "Gastos Hormiga" -> R.drawable.hormiga
            "Mercado" -> R.drawable.mercado
            "Deudas" -> R.drawable.deudis
            else -> {
                // Manejar otras categorías si es necesario
                R.drawable.savings_24dp_321d71
            }
        }
        // Asigna la imagen al ImageView del ViewHolder
        holder.categoriaImageView.setImageResource(imagenResId)

        holder.itemView.setOnClickListener {
            listener2?.onItemClick2(currentGasto)
        }
    }

    fun setOnItemClickListener2(listener2: OnItemClickListener2) {
        this.listener2 = listener2
    }

    override fun getItemCount() = gastos.size

    inner class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nombregasto)
        val valorTextView: TextView = itemView.findViewById(R.id.valor)
        val categoria: TextView = itemView.findViewById(R.id.categoria)
        val fecha: TextView = itemView.findViewById(R.id.fecha)
        val cv: View = itemView.findViewById(R.id.cv)
        val categoriaImageView: ImageView = itemView.findViewById(R.id.iconImageView)

    }

}
