package com.practica.finazapp.Vista

import com.practica.finazapp.Entidades.IngresoDTO

interface IngresosListener {

    fun onIngresosCargados(totalIngresos: Double, ingresosMensuales: List<IngresoDTO>, ingresosCasuales: List<IngresoDTO>)
}
