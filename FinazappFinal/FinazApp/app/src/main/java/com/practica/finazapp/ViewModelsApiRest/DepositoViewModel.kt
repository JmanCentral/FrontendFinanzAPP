package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.practica.finazapp.Entidades.DepositoDTO
import com.practica.finazapp.RepositoriosApiRest.DepositoRepository
import kotlinx.coroutines.launch

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

    private val _valorGastosMesDepositoLiveData = MutableLiveData<Double?>()
    val valorGastosMesDepositoLiveData: LiveData<Double?> =
        _valorGastosMesDepositoLiveData.distinctUntilChanged()

    private val _operacionCompletada = MutableLiveData<Boolean>()
    val operacionCompletada: LiveData<Boolean> = _operacionCompletada

    fun registrarDeposito(depositoDTO: DepositoDTO, idUsuario: Long, idAlcancia: Long) {
        depositoRepository.registrarDeposito(
            depositoDTO,
            idUsuario,
            idAlcancia
        ) { response, error ->
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

    fun obtenerValorGastosMesDeposito(idUsuario: Long) {
        depositoRepository.obtenerValorGastosMesDeposito(idUsuario) { valor, error ->
            if (error == null) {
                _valorGastosMesDepositoLiveData.postValue(valor)
            } else {
                _errorLiveData.postValue(error)
            }
        }

    }

    fun modificarDepositos(depositoDTO: DepositoDTO, idDeposito: Long, idAlcancia: Long) {
        viewModelScope.launch {
            depositoRepository.modificarDepositos(
                depositoDTO,
                idDeposito,
                idAlcancia
            ) { resultado, error ->
                if (error == null) {
                    _operacionCompletada.postValue(true)
                } else {
                    _errorLiveData.postValue(error)
                }
            }

        }
    }

        fun eliminarDepositos(idDeposito: Long, idAlcancia: Long) {
            viewModelScope.launch {
                depositoRepository.eliminarDepositos(idDeposito, idAlcancia) { resultado, error ->
                    if (error == null) {
                        _operacionCompletada.postValue(true)
                    } else {
                        _errorLiveData.postValue(error)
                    }
                }
            }
        }
    }

