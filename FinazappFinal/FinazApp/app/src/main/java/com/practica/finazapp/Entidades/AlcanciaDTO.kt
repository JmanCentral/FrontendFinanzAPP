package com.practica.finazapp.Entidades

import java.io.File

data class AlcanciaDTO(
    val idAlcancia: Long? = null,
    val nombre_alcancia: String,
    val meta: Double,
    val saldoActual: Double,
    val codigo: String,
    val filePath: String? = null,
    val fechaCreacion: String
)
