package com.practica.finazapp.Entidades

data class UsuarioDTO (

    var id_usuario: Long,
    var username: String,
    var contrasena: String,
    var nombre: String,
    var email: String,
    var apellido: String,
    var token: String,
    val roles: Set<String>

)