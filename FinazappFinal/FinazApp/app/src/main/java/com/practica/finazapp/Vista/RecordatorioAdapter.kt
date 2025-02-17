package com.practica.finazapp.Vista

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.RecordatorioDTO
import com.practica.finazapp.R
import java.text.NumberFormat

class RecordatorioAdapter (private val recordatorios: List<RecordatorioDTO>) :

    RecyclerView.Adapter<RecordatorioAdapter.GastoViewHolder>() {

    private var listener3: RecordatorioListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listrecordatorio, parent, false)
        return GastoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        holder.cv2.setAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_transition))

        val currentRecordatorio = recordatorios[position]
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        holder.nameTextView.text = currentRecordatorio.nombre
        holder.valorTextView.text = "${numberFormat.format(currentRecordatorio.valor)}$"
        holder.estado.text = currentRecordatorio.estado
        holder.dias.text = "Recordatorio programado cada ${numberFormat.format(currentRecordatorio.dias_recordatorio / 60000)} minutos"
        holder.fecha.text = currentRecordatorio.fecha

        // Cambiar color si el estado es "pagado" o "vencido"
        if (currentRecordatorio.estado.equals("vencido", ignoreCase = true)) {
            holder.estado.setTextColor(holder.itemView.context.getColor(R.color.rojo)) // Asegúrate de definir este color en colors.xml
        } else if (currentRecordatorio.estado.equals("pagado", ignoreCase = true)) {
            holder.estado.setTextColor(holder.itemView.context.getColor(R.color.verde)) // Color por defecto
        } else {
            holder.estado.setTextColor(holder.itemView.context.getColor(R.color.negro)) // Color por defecto
        }
        holder.itemView.setOnClickListener {
            listener3?.onItemClick3(currentRecordatorio)
        }
    }


    fun setOnItemClickListener3(listener3: RecordatorioListener) {
        this.listener3 = listener3
    }

    override fun getItemCount() = recordatorios.size

    inner class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nombrerecordatorio)
        val valorTextView: TextView = itemView.findViewById(R.id.valor)
        val estado: TextView = itemView.findViewById(R.id.Estado)
        val dias: TextView = itemView.findViewById(R.id.dias)
        val fecha: TextView = itemView.findViewById(R.id.fecha)
        val cv2: View = itemView.findViewById(R.id.cv2)

    }
}