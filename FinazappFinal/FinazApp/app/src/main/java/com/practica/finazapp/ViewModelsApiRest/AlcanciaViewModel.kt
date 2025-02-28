package com.example.finanzapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.finanzapp.repositories.AlcanciaRepository
import com.practica.finazapp.Entidades.AlcanciaDTO
import com.practica.finazapp.Entidades.GastoDTO
import kotlinx.coroutines.launch

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

    private val _operacionCoMPLETADALiveData = MutableLiveData<Boolean>()
    val operacionCompletadaLiveData: LiveData<Boolean> = _operacionCoMPLETADALiveData

    // Registrar alcancía
    fun registrarAlcancia(alcancia: AlcanciaDTO, idUsuario: Long) {
        alcanciaRepository.registrarAlcancia(alcancia, idUsuario) { alcanciaResponse, error ->
            if (alcanciaResponse != null) {
                _operacionCoMPLETADALiveData.postValue(true)
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

    // Función para modificar un gasto
    fun modificarAlcancia(idAlcancia: Long, alcancia: AlcanciaDTO) {
        viewModelScope.launch {
            alcanciaRepository.actualizarAlcancia(idAlcancia, alcancia) { resultado, error ->
                if (error == null) {
                    _operacionCoMPLETADALiveData.postValue(true)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }

    // Función para eliminar un gasto
    fun eliminarAlcancia(idAlcancia: Long) {
        viewModelScope.launch {
            alcanciaRepository.eliminarAlcancia(idAlcancia) { resultado, error ->
                if (error == null) {
                    _operacionCoMPLETADALiveData.postValue(true)
                } else {
                    _errorLiveData.postValue(error)
                }
            }
        }
    }
}
