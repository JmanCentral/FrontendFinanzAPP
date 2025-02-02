package com.practica.finazapp.Entidades

data class IngresoDTO(

    var id_ingreso: Long,
    var nombre_ingreso: String,
    var valor: Double,
    var fecha: String,
    var tipo_ingreso: String

)

