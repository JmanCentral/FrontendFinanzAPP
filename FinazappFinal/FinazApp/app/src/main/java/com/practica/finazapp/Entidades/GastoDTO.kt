package com.practica.finazapp.Entidades

data class GastoDTO(

    var id_gasto: Long,
    var nombre_gasto: String,
    var categoria: String,
    var fecha: String,
    var valor: Double
)