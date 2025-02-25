package com.practica.finazapp.Vista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.R
import java.text.NumberFormat


class GastoListaAdapter(private val gastos: List<GastoDTO>) :
    RecyclerView.Adapter<GastoListaAdapter.GastoListaViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoListaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_lista, parent, false)
        return GastoListaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GastoListaViewHolder, position: Int) {

        holder.ViewCardview.setAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_transition))

        val currentGasto = gastos[position]
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        holder.descripcionTextView.text = currentGasto.nombre_gasto
        holder.categoriaTextView.text = currentGasto.categoria
        holder.fechaTextView.text = currentGasto.fecha
        holder.valorTextView.text = "${numberFormat.format(currentGasto.valor)}$"

        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentGasto)
        }
    }

    override fun getItemCount() = gastos.size

    inner class GastoListaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descripcionTextView: TextView = itemView.findViewById(R.id.Viewdescripcion)
        val categoriaTextView: TextView = itemView.findViewById(R.id.Viewcategoria)
        val fechaTextView: TextView = itemView.findViewById(R.id.Viewfecha)
        val valorTextView: TextView = itemView.findViewById(R.id.Viewvalor)
        val ViewCardview: CardView = itemView.findViewById(R.id.cvlistagastos)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
