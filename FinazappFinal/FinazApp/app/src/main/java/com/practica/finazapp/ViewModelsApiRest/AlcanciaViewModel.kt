package com.example.finanzapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.finanzapp.repositories.AlcanciaRepository
import com.practica.finazapp.Entidades.AlcanciaDTO

class AlcanciaViewModel(application: Application) : AndroidViewModel(application) {

    private val alcanciaRepository: AlcanciaRepository by lazy {
        AlcanciaRepository(getApplication<Application>().applicationContext)
    }

    private val _alcanciaLiveData = MutableLiveData<AlcanciaDTO?>()
    val alcanciaLiveData: LiveData<AlcanciaDTO?> = _alcanciaLiveData

    private val _alcanciacodigoLiveData = MutableLiveData<List<AlcanciaDTO>?>()
    val alcanciacodigoLiveData: LiveData<List<AlcanciaDTO>?> = _alcanciacodigoLiveData

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> = _errorLiveData

    private val _alcanciasPorUserLiveData = MutableLiveData<List<AlcanciaDTO>?>()
    val alcanciasPorUserLiveData: LiveData<List<AlcanciaDTO>?> = _alcanciasPorUserLiveData

    // Registrar alcancía
    fun registrarAlcancia(alcancia: AlcanciaDTO, idUsuario: Long) {
        alcanciaRepository.registrarAlcancia(alcancia, idUsuario) { alcanciaResponse, error ->
            if (alcanciaResponse != null) {
                _alcanciaLiveData.postValue(alcanciaResponse)
            }
            _errorLiveData.postValue(error)
        }
    }

    fun buscarAlcanciaPorCodigo(codigo: String) {
        alcanciaRepository.buscarPorCodigo(codigo) { alcanciaResponse, error ->
            _alcanciacodigoLiveData.postValue(alcanciaResponse)
            _errorLiveData.postValue(error)
        }
    }

    fun obtenerAlcanciasPorUser(idUsuario: Long) {
        alcanciaRepository.obtenerAlcanciasporuser(idUsuario) { alcanciasResponse, error ->
            _alcanciasPorUserLiveData.postValue(alcanciasResponse)
            _errorLiveData.postValue(error)
        }
    }
}
