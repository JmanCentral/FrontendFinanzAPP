package com.practica.finazapp.Vista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.ConsejosDTO
import com.practica.finazapp.R

class ConsejosAdapter(private val consejosList: List<ConsejosDTO>) : RecyclerView.Adapter<ConsejosAdapter.ConsejosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsejosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listconsejos, parent, false)
        return ConsejosViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsejosViewHolder, position: Int) {

        holder.cv.setAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_transition))

        val consejo = consejosList[position]
        holder.consejoTextView.text = consejo.consejo

    }

    override fun getItemCount(): Int {
        return consejosList.size
    }

    class ConsejosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val consejoTextView: TextView = itemView.findViewById(R.id.consejoTextView)
        val cv: View = itemView.findViewById(R.id.cv_consejos)
    }
}