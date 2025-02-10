package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.practica.finazapp.Entidades.LoginDTO
import com.practica.finazapp.Entidades.LoginResponseDTO
import com.practica.finazapp.Entidades.UsuarioDTO
import com.practica.finazapp.Repositories.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository by lazy {
        UserRepository(getApplication<Application>().applicationContext)
    }

    private val _usuarioLiveData = MutableLiveData<UsuarioDTO?>()
    val usuarioLiveData: LiveData<UsuarioDTO?> = _usuarioLiveData

    private val _loginResponse = MutableLiveData<LoginResponseDTO?>()
    val loginResponse: LiveData<LoginResponseDTO?> = _loginResponse

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> = _errorLiveData.distinctUntilChanged()

    fun registrarUsuario(usuario: UsuarioDTO) {
        userRepository.registrarUsuario(usuario) { usuarioResponse, error ->
            _usuarioLiveData.postValue(usuarioResponse)
            _errorLiveData.postValue(error)
        }
    }

    fun iniciarSesion(loginDTO: LoginDTO) {
        userRepository.iniciarSesion(loginDTO.username, loginDTO.contrasena) { response, error ->
            if (response != null) {
                _loginResponse.postValue(response)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    fun ObtenerUsuario(idUsuario: Long) {
        userRepository.ObtenerUsuario(idUsuario) { usuarioResponse, error ->
            _usuarioLiveData.postValue(usuarioResponse)
            _errorLiveData.postValue(error)
        }
    }
}
