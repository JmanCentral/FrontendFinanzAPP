package com.practica.finazapp.Vista

import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.Entidades.GastoDTO

interface OnItemClickListener {
    fun onItemClick(gasto: GastoDTO)
}