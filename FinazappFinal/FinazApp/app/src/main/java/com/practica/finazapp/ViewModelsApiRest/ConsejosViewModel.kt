package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.Entidades.ConsejosDTO
import com.practica.finazapp.RepositoriosApiRest.ConsejosRepository

class ConsejosViewModel(application: Application) : AndroidViewModel(application) {

    private val consejosRepository: ConsejosRepository by lazy {
        ConsejosRepository(getApplication<Application>().applicationContext)
    }

    private val _consejosResponse = MutableLiveData<List<ConsejosDTO>?>()
    val consejosResponse: LiveData<List<ConsejosDTO>?> get() = _consejosResponse

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> get() = _errorLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun obtenerConsejos() {

        consejosRepository.ObtenerConsejos() { response, error ->
            if (response != null) {
                _consejosResponse.postValue(response)
                _errorLiveData.postValue(null)
            } else {
                _errorLiveData.postValue(error)
            }
        }
    }

}