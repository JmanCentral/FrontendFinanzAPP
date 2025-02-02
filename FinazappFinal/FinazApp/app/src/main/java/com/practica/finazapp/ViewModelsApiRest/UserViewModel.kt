package com.practica.finazapp.ViewModelsApiRest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practica.finazapp.Entidades.UsuarioDTO
import com.practica.finazapp.Repositories.UserRepository

class UserViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _usuarioLiveData = MutableLiveData<UsuarioDTO?>()
    val usuarioLiveData: LiveData<UsuarioDTO?> = _usuarioLiveData

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> = _errorLiveData

    fun registrarUsuario(usuario: UsuarioDTO) {
        userRepository.registrarUsuario(usuario) { usuarioResponse, error ->
            _usuarioLiveData.postValue(usuarioResponse)
            _errorLiveData.postValue(error)
        }
    }

    fun loginUsuario(usuario: UsuarioDTO) {
        userRepository.loginUsuario(usuario) { usuarioResponse, error ->
            _usuarioLiveData.postValue(usuarioResponse)
            _errorLiveData.postValue(error)
        }
    }

    fun ObtenerUsuario(idUsuario: Long) {
        userRepository.ObtenerUsuario(idUsuario) { usuarioResponse, error ->
            _usuarioLiveData.postValue(usuarioResponse)
            _errorLiveData.postValue(error)
        }
    }
}
