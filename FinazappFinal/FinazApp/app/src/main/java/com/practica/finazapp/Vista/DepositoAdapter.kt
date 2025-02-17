package com.practica.finazapp.Vista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.DepositoDTO
import com.practica.finazapp.R
import java.text.NumberFormat

class DepositoAdapter(private var depositos: List<DepositoDTO>) :
    RecyclerView.Adapter<DepositoAdapter.DepositoViewHolder>() {

    private var listener: DepositoListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deposito, parent, false)
        return DepositoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DepositoViewHolder, position: Int) {
        val currentDeposito = depositos[position]
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        // Configurar los datos en las vistas
        holder.nombreDepositanteTextView.text = currentDeposito.nombre_depositante
        holder.montoTextView.text = "Monto: ${numberFormat.format(currentDeposito.monto)}$"
        holder.fechaTextView.text = "Fecha: ${currentDeposito.fecha}"

        // Configurar la barra de progreso
        val maxMonto = 1000.0 // Define un valor máximo para la barra de progreso
        val progreso = (currentDeposito.monto / maxMonto) * 100
        holder.progressBarMonto.progress = progreso.toInt()

        // Manejar clic en el elemento
        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentDeposito)
        }
    }

    override fun getItemCount() = depositos.size

    fun updateList(newDepositos: List<DepositoDTO>) {
        this.depositos = newDepositos
        notifyDataSetChanged() // Notificar al RecyclerView que los datos han cambiado
    }

    fun setOnItemClickListener(listener: DepositoListener) {
        this.listener = listener
    }

    inner class DepositoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreDepositanteTextView: TextView = itemView.findViewById(R.id.tvNombreDepositante)
        val montoTextView: TextView = itemView.findViewById(R.id.tvMonto)
        val fechaTextView: TextView = itemView.findViewById(R.id.tvFecha)
        val progressBarMonto: ProgressBar = itemView.findViewById(R.id.progressBarMonto)
    }
}