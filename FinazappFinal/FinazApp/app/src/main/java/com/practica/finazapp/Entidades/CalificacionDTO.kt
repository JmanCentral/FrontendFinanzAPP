package com.practica.finazapp.Entidades

data class CalificacionDTO(
    val idCalificacion: Long?,
    val me_gusta: Int,
    val no_me_gusta: Int,
    val id_usuario: Long?,
    val idConsejo: Long?
)