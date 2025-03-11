package com.practica.finazapp.Vista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.practica.finazapp.Entidades.CalificacionDTO
import com.practica.finazapp.Entidades.ConsejosDTO
import com.practica.finazapp.R

class ConsejosAdapter(
    private val consejosList: List<ConsejosDTO>,
    private val calificacionesMap: Map<Long, CalificacionDTO>,
    private val onLikeClick: (ConsejosDTO) -> Unit,
    private val onDislikeClick: (ConsejosDTO) -> Unit
) : RecyclerView.Adapter<ConsejosAdapter.ConsejosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsejosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listconsejos, parent, false)
        return ConsejosViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsejosViewHolder, position: Int) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_transition))

        val consejo = consejosList[position]
        val calificacion = calificacionesMap[consejo.idConsejo] // Buscar la calificación en el mapa

        holder.consejoTextView.text = consejo.consejo
        holder.likesTextView.text = calificacion?.me_gusta.toString() ?: "0"
        holder.dislikesTextView.text = calificacion?.no_me_gusta.toString() ?: "0"

        // Manejo de eventos para dar me gusta o no me gusta
        holder.likeButton.setOnClickListener { onLikeClick(consejo) }
        holder.dislikeButton.setOnClickListener { onDislikeClick(consejo) }
    }

    override fun getItemCount(): Int {
        return consejosList.size
    }

    class ConsejosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val consejoTextView: TextView = itemView.findViewById(R.id.consejoTextView)
        val likesTextView: TextView = itemView.findViewById(R.id.texto1)
        val dislikesTextView: TextView = itemView.findViewById(R.id.texto2)
        val likeButton: LottieAnimationView = itemView.findViewById(R.id.lottie_imagen1)
        val dislikeButton: LottieAnimationView = itemView.findViewById(R.id.lottie_imagen2)
        val cv: View = itemView.findViewById(R.id.cv_consejos)
    }
}
