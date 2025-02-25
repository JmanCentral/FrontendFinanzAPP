package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.Entidades.EmailRequest
import com.practica.finazapp.Entidades.UsuarioDTO
import com.practica.finazapp.RepositoriosApiRest.PasswordRepository

class PasswordViewModel (application: Application) : AndroidViewModel(application)  {

    private val passwordRepository: PasswordRepository by lazy {
        PasswordRepository(getApplication<Application>().applicationContext)
    }

    private val _passwordLiveData = MutableLiveData<EmailRequest?>()
    val passwordLiveData : LiveData<EmailRequest?> = _passwordLiveData

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> = _errorLiveData

    fun recuperarpassword(recuperador: EmailRequest) {
        passwordRepository.recuperarContrasena(recuperador) { recuperadorResponse, error ->
            if (error.isNullOrEmpty()) {  // ✅ Solo actualizar si no hay error
                _passwordLiveData.postValue(recuperadorResponse)
            }
                _errorLiveData.postValue(error)
        }
    }
}