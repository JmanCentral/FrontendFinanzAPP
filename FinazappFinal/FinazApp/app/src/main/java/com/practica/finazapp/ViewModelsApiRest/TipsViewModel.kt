package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.Entidades.GastoDTO
import com.practica.finazapp.Entidades.TipsDTO
import com.practica.finazapp.RepositoriosApiRest.DepositoRepository
import com.practica.finazapp.RepositoriosApiRest.TipsRepository

class TipsViewModel (application: Application) : AndroidViewModel(application) {

    private val tipsRepository: TipsRepository by lazy {
        TipsRepository(getApplication<Application>().applicationContext)
    }

    private val _obtenertips = MutableLiveData<List<TipsDTO>?>()
    val obtenertips: LiveData<List<TipsDTO>?> = _obtenertips

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?> = _errorLiveData

    fun obtenerTips(idUsuario: Long) {
        _isLoading.value = true
        tipsRepository.obteherTips(idUsuario) {frecuentes, error ->
            if (error == null) {
                _obtenertips.postValue(frecuentes)
            } else {
                _errorLiveData.postValue(error)
            }
            _isLoading.postValue(false)
        }
    }

}