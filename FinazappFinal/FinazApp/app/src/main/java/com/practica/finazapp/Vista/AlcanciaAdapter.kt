package com.practica.finazapp.Vista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.R
import java.text.NumberFormat

class AlcanciaAdapter(private val alcancias: List<AlcanciaDTO>) :
    RecyclerView.Adapter<AlcanciaAdapter.AlcanciaViewHolder>() {

    private var listener: AlcanciaListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcanciaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alcancia, parent, false) // Cambia "item_alcancia" por tu layout
        return AlcanciaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlcanciaViewHolder, position: Int) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_transition))

        val currentAlcancia = alcancias[position]
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        // Configurar los datos en las vistas
        holder.nombreTextView.text = currentAlcancia.nombre_alcancia
        holder.metaTextView.text = "Meta: ${numberFormat.format(currentAlcancia.meta)}$"
        holder.saldoActualTextView.text = "Saldo actual: ${numberFormat.format(currentAlcancia.saldoActual)}$"
        holder.codigoTextView.text = "Código: ${currentAlcancia.codigo}"
        holder.fechaCreacionTextView.text = "Fecha de creación: ${currentAlcancia.fechaCreacion}"

        // Calcular el progreso de la meta
        val progreso = if (currentAlcancia.meta > 0) {
            (currentAlcancia.saldoActual / currentAlcancia.meta) * 100
        } else {
            0.0
        }
        holder.progressBarMeta.progress = progreso.toInt()
        holder.progresoMetaTextView.text = "${progreso.toInt()}% completado"

        // Cambiar el color del texto del progreso según el estado
        if (progreso >= 100) {
            holder.progresoMetaTextView.setTextColor(holder.itemView.context.getColor(R.color.verde)) // Meta alcanzada
        } else {
            holder.progresoMetaTextView.setTextColor(holder.itemView.context.getColor(R.color.negro)) // Meta en progreso
        }

        // Manejar el clic en el elemento (ver detalles)
        holder.itemView.setOnClickListener {
            listener?.onItemClick5(currentAlcancia)
        }

        // Manejar el clic en el botón de modificar
        holder.btnModificar.setOnClickListener {
            listener?.onItemClickModificar(currentAlcancia)
        }
    }

    override fun getItemCount() = alcancias.size

    fun setOnItemClickListener(listener: AlcanciaListener) {
        this.listener = listener
    }

    inner class AlcanciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.tvNombreAlcancia)
        val metaTextView: TextView = itemView.findViewById(R.id.tvMeta)
        val saldoActualTextView: TextView = itemView.findViewById(R.id.tvSaldoActual)
        val codigoTextView: TextView = itemView.findViewById(R.id.tvCodigo)
        val fechaCreacionTextView: TextView = itemView.findViewById(R.id.tvFechaCreacion)
        val progressBarMeta: ProgressBar = itemView.findViewById(R.id.progressBarMeta)
        val progresoMetaTextView: TextView = itemView.findViewById(R.id.tvProgresoMeta)
        val cv: View = itemView.findViewById(R.id.cv) // Contenedor principal para la animación
        val btnModificar: ImageView = itemView.findViewById(R.id.btnModificar) // Botón para modificar
    }
}