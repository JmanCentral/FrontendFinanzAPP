package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.Entidades.CalificacionDTO
import com.practica.finazapp.Entidades.DepositoDTO
import com.practica.finazapp.RepositoriosApiRest.CalificacionRepository

class CalificacionViewModel (application: Application) : AndroidViewModel(application) {

    private val calificacionRepository: CalificacionRepository by lazy {
        CalificacionRepository(getApplication<Application>().applicationContext)
    }

    private val _calificacionResponse = MutableLiveData<CalificacionDTO?>()
    val calificacionResponse: LiveData<CalificacionDTO?> get() = _calificacionResponse

    private val _calificacionList = MutableLiveData<List<CalificacionDTO>?>()
    val calificacionList: LiveData<List<CalificacionDTO>?> get() = _calificacionList

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> get() = _errorLiveData

    private val _operacionCompletada = MutableLiveData<Boolean>()
    val operacionCompletada: LiveData<Boolean> = _operacionCompletada


    fun registrarCalificacion(calificacionDTO: CalificacionDTO) {
        calificacionRepository.registrarCalificacion(calificacionDTO) { response, error ->
            if (response != null) {
                _operacionCompletada.postValue(true)
            } else {
                _errorLiveData.postValue(error)
                _calificacionResponse.postValue(null)
            }
        }
    }

    fun obtenerCalificacion() {
        calificacionRepository.obtenerCalificaciones() { response, error ->
            if (response != null) {
                _calificacionList.postValue(response)
                _errorLiveData.postValue(null)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }
}