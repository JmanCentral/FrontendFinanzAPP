package com.practica.finazapp.ViewModelsApiRest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.Entidades.CalificacionDTO
import com.practica.finazapp.RepositoriosApiRest.CalificacionRepository

class CalificacionViewModel(application: Application) : AndroidViewModel(application) {

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

    // Nuevo LiveData para notificar la posición del elemento actualizado
    private val _posicionActualizada = MutableLiveData<Int>()
    val posicionActualizada: LiveData<Int> get() = _posicionActualizada


    fun registrarCalificacion(calificacionDTO: CalificacionDTO, position: Int) {
        calificacionRepository.registrarCalificacion(calificacionDTO) { response, error ->
            if (response != null) {
                val listaActual = _calificacionList.value?.toMutableList() ?: mutableListOf()

                // Eliminar cualquier calificación anterior para el mismo consejo y usuario
                val nuevaLista = listaActual.filterNot {
                    it.id_usuario == calificacionDTO.id_usuario && it.idConsejo == calificacionDTO.idConsejo
                }.toMutableList()

                // Agregar la nueva calificación
                nuevaLista.add(calificacionDTO)

                _calificacionList.postValue(nuevaLista)
                _operacionCompletada.postValue(true)
                _posicionActualizada.postValue(position)
            } else {
                _errorLiveData.postValue(error)
                _calificacionResponse.postValue(null)
            }
        }
    }



    fun haDadoMeGusta(usuarioId: Long, consejoId: Long): Boolean {
        // Obtener la lista actual de calificaciones
        val calificaciones = _calificacionList.value

        // Verificar si el usuario ya ha dado "me gusta" a este consejo
        return calificaciones?.any { calificacion ->
            calificacion.id_usuario == usuarioId && calificacion.idConsejo == consejoId && calificacion.me_gusta == 1
        } ?: false
    }

    fun haDadoNoMeGusta(usuarioId: Long, consejoId: Long): Boolean {
        // Obtener la lista actual de calificaciones
        val calificaciones = _calificacionList.value

        // Verificar si el usuario ya ha dado "me gusta" a este consejo
        return calificaciones?.any { calificacion ->
            calificacion.id_usuario == usuarioId && calificacion.idConsejo == consejoId && calificacion.no_me_gusta == 1
        } ?: false
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