package com.practica.finazapp.Entidades

data class AlcanciaDTO(
    val idAlcancia: Long,
    val nombre_alcancia: String,
    val meta: Double,
    val saldoActual: Double,
    val codigo: String,
    val fechaCreacion: String
)