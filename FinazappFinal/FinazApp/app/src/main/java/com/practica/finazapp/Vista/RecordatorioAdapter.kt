package com.practica.finazapp.Vista

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
        holder.dias.text = "Recordatorio programado cada ${numberFormat.format(currentRecordatorio.dias_recordatorio * 1440)} minutos"
        holder.fecha.text = currentRecordatorio.fecha


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