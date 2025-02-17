package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practica.finazapp.Entidades.DepositoDTO
import com.practica.finazapp.RepositoriosApiRest.DepositoRepository

class DepositoViewModel(application: Application) : AndroidViewModel(application) {

    private val depositoRepository: DepositoRepository by lazy {
        DepositoRepository(getApplication<Application>().applicationContext)
    }

    private val _depositoResponse = MutableLiveData<DepositoDTO?>()
    val depositoResponse: LiveData<DepositoDTO?> get() = _depositoResponse

    private val _depositosList = MutableLiveData<List<DepositoDTO>?>()
    val depositosList: LiveData<List<DepositoDTO>?> get() = _depositosList

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> get() = _errorLiveData

    private val _operacionCompletada = MutableLiveData<Boolean>()
    val operacionCompletada: LiveData<Boolean> = _operacionCompletada

    fun registrarDeposito(depositoDTO: DepositoDTO, idUsuario: Long, idAlcancia: Long) {
        depositoRepository.registrarDeposito(depositoDTO, idUsuario, idAlcancia) { response, error ->
            if (response != null) {
                _operacionCompletada.postValue(true)
            } else {
                _errorLiveData.postValue(error)
                _depositoResponse.postValue(null)
            }
        }
    }

    fun obtenerDepositos(idAlcancia: Long) {
        depositoRepository.obtenerDepositos(idAlcancia) { response, error ->
            if (response != null) {
                _depositosList.postValue(response)
                _errorLiveData.postValue(null)
            } else {
                _errorLiveData.postValue(error)
                _depositosList.postValue(null)
            }
        }
    }
}
