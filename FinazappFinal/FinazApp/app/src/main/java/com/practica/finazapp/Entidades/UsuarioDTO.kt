package com.practica.finazapp.Entidades

data class UsuarioDTO (

    var id_usuario: Long,
    var username: String,
    var contrasena: String,
    var nombre: String,
    var apellido: String,
    val roles: Set<String>

)