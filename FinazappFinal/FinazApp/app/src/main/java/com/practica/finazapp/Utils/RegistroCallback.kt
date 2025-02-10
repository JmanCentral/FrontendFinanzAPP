package com.practica.finazapp.Utils

import com.practica.finazapp.Entidades.UsuarioDTO

interface RegistroCallback {
    fun onSuccess(usuario: UsuarioDTO)
    fun onError(mensaje: String)
    fun onFailure(error: String)
}
