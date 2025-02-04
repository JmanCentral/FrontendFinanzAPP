package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.Entidades.AlertaDTO
import com.practica.finazapp.RepositoriosApiRest.AlertRepository

class AlertViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: AlertRepository by lazy {
        AlertRepository(getApplication<Application>().applicationContext)
    }


    private val _operacionCompletada = MutableLiveData<Boolean>()
    val operacionCompletada: LiveData<Boolean> = _operacionCompletada

    private val _alertasPorAnioLiveData = MutableLiveData<List<AlertaDTO>?>()
    val alertasPorAnioLiveData: LiveData<List<AlertaDTO>?> = _alertasPorAnioLiveData

    private val _alertasPorMesLiveData = MutableLiveData<List<AlertaDTO>?>()
    val alertasPorMesLiveData: LiveData<List<AlertaDTO>?> = _alertasPorMesLiveData

   private val _alertasPorUserLiveData = MutableLiveData<List<AlertaDTO>?>()
    val alertasPorUserLiveData: LiveData<List<AlertaDTO>?> = _alertasPorUserLiveData

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> = _errorLiveData


    fun registrarAlerta(idUsuario: Long, alerta: AlertaDTO) {
        repository.registrarAlerta(idUsuario, alerta) { resultado, error ->
            if (error == null) {
                _operacionCompletada.postValue(true) // Notificar que la operación se completó
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    fun obtenerAlertaPorUser(idUsuario: Long) {
        repository.obtenerAlertaPorUser(idUsuario) { alertas, error ->
            if (error == null) {
                _alertasPorUserLiveData.postValue(alertas)
    }
            else {
                _errorLiveData.postValue(error)
            }
        }

            }

    fun obtenerAlertaPorAnio(idUsuario: Long) {
        repository.obtenerAlertaPorAnio(idUsuario) { alertas, error ->
            if (error == null) {
                _alertasPorAnioLiveData.postValue(alertas)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    fun obtenerAlertaPorMes(idUsuario: Long) {
        repository.obtenerAlertaPorMes(idUsuario) { alertas, error ->
            if (error == null) {
                _alertasPorMesLiveData.postValue(alertas)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

    fun modificarAlerta(idAlerta: Long, alertaDTO: AlertaDTO) {
        repository.modificarAlerta(idAlerta, alertaDTO) { resultado, error ->
            if (error == null) {
                _operacionCompletada.postValue(true) // Notificar que la operación se completó
    } else {
                _errorLiveData.postValue(error)
            }
        }

            }

    fun eliminarAlerta(idAlerta: Long) {
        repository.eliminarAlerta(idAlerta) { exito, error ->
            if (exito) {
                _operacionCompletada.postValue(true) // Notificar que la operación se completó
                } else {
                _errorLiveData.postValue(error)
            }
        }

    }

}