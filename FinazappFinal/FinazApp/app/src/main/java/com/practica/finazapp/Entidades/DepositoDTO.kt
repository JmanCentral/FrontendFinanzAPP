package com.practica.finazapp.Entidades

data class DepositoDTO(
    val idDeposito: Long,
    val monto: Double,
    val nombre_depositante: String,
    val fecha: String
)