package com.practica.finazapp.Vista

import com.practica.finazapp.Entidades.AlertaDTO

interface AlertasListener {

    fun onAlertasCargadas(alertas: List<AlertaDTO> , ingresoTotal: Double)
}