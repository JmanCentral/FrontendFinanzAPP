package com.practica.finazapp.Entidades

data class RecordatorioDTO(

    val id_recordatorio: Long,
    val nombre: String,
    val estado: String,
    val fecha: String,
    val dia: Int,
    val valor: Double
)
