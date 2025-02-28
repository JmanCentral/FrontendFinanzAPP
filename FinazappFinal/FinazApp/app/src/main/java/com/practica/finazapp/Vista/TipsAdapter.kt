package com.practica.finazapp.Vista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cooltechworks.views.ScratchImageView
import com.practica.finazapp.Entidades.TipsDTO
import com.practica.finazapp.R

class TipsAdapter(
    private val tips: List<TipsDTO>,
    private val listener: OnTipClickListener
) : RecyclerView.Adapter<TipsAdapter.AlcanciaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlcanciaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.listtips, parent, false)
        return AlcanciaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlcanciaViewHolder, position: Int) {
        holder.cv.setAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_transition)
        )

        val currentTips = tips[position]

        // Configurar los datos en las vistas
        holder.titulo.text = currentTips.titulo
        holder.tip.text = currentTips.contenido

        // Reiniciar el ScratchImageView
        resetScratchView(holder.scratchView)

        // Configurar el listener del ScratchImageView
        holder.scratchView.setRevealListener(object : ScratchImageView.IRevealListener {
            override fun onRevealed(scratchImageView: ScratchImageView) {
                Toast.makeText(holder.itemView.context, "¡Consejo revelado!", Toast.LENGTH_SHORT).show()
                listener.onTipClicked(currentTips) // Llamamos al listener cuando se revele el tip
            }

            override fun onRevealPercentChangedListener(siv: ScratchImageView?, percent: Float) {
                // Si el usuario ha raspado más del 50%, revelamos completamente el ScratchImageView
                if (percent >= 0.5f) {
                    siv?.reveal() // Revela completamente el ScratchImageView
                }
            }
        })
    }

    override fun getItemCount() = tips.size

    // Función para reiniciar el ScratchImageView
    private fun resetScratchView(scratchView: ScratchImageView) {
        val parent = scratchView.parent as ViewGroup
        val index = parent.indexOfChild(scratchView)
        parent.removeView(scratchView) // Eliminar el ScratchImageView actual

        val newScratchView = ScratchImageView(scratchView.context).apply {
            layoutParams = scratchView.layoutParams
            id = R.id.scratchView // Asegúrate de que el ID sea el mismo
            scaleType = ImageView.ScaleType.FIT_XY
        }

        parent.addView(newScratchView, index) // Agregar el nuevo ScratchImageView
    }

    inner class AlcanciaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tituloTextView)
        val tip: TextView = itemView.findViewById(R.id.subtituloTextView)
        val cv: View = itemView.findViewById(R.id.cardviewTips)
        val scratchView: ScratchImageView = itemView.findViewById(R.id.scratchView)
    }
}
