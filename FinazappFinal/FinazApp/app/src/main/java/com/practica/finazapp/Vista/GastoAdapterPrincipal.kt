package com.practica.finazapp.Vista

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val currentGasto = gastos[position]
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        holder.nameTextView.text = currentGasto.nombre_gasto
        holder.valorTextView.text = "${numberFormat.format(currentGasto.valor)}$"
        holder.categoria.text = currentGasto.categoria
        holder.fecha.text = currentGasto.fecha

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
    }
}
