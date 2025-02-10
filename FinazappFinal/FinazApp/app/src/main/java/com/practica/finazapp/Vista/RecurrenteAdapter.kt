package com.practica.finazapp.Vista

import com.practica.finazapp.Entidades.ProyeccionDTO
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.R
import java.text.NumberFormat

class RecurrenteAdapter (private val recurrentes: List<ProyeccionDTO>) :

    RecyclerView.Adapter<RecurrenteAdapter.GastoViewHolder>() {

    private var listener4: RecurrenteListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listrecurrente, parent, false)
        return GastoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {

        holder.cv3.setAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_transition))

        val currentRecurrentes = recurrentes[position]
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        // Formateamos la cantidad
        val cantidadConvertida = numberFormat.format(currentRecurrentes.cantidad)


        holder.nameTextView.text = currentRecurrentes.descripcion
        holder.valorTextView.text = "La cantidad del gasto recurrente es: $cantidadConvertida"
        holder.estado.text = "${numberFormat.format(currentRecurrentes.total)} $"



        holder.itemView.setOnClickListener {
            listener4?.onItemClick4(currentRecurrentes)
        }
    }

    override fun getItemCount() = recurrentes.size

    inner class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.descripcionrecurrente)
        val valorTextView: TextView = itemView.findViewById(R.id.cantidad1)
        val estado: TextView = itemView.findViewById(R.id.Total)
        val cv3: View = itemView.findViewById(R.id.cv3)

    }
}