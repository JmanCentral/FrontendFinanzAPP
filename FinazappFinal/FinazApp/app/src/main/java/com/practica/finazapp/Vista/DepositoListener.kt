package com.practica.finazapp.Vista

import com.practica.finazapp.Entidades.DepositoDTO

interface DepositoListener {
    fun onItemClick(deposito: DepositoDTO)
}