package com.practica.finazapp.Vista

import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.Entidades.AlertaDTO
import com.practica.finazapp.Entidades.RecordatorioDTO

interface AlcanciaListener {

    fun onItemClick5(alcancia: AlcanciaDTO)

    fun onItemClickModificar(alcancia: AlcanciaDTO)

}