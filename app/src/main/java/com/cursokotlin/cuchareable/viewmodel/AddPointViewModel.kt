package com.cursokotlin.cuchareable.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.cursokotlin.cuchareable.AuthManager
import com.cursokotlin.cuchareable.model.Point
import com.cursokotlin.cuchareable.repository.RepositoryUsuario
import kotlinx.coroutines.launch

class AddPointViewModel(private val repository: RepositoryUsuario) : ViewModel() {

    private val _addPointResult = MutableLiveData<ResultState<Boolean>>()
    val addPointResult: LiveData<ResultState<Boolean>> = _addPointResult

    private val _myPointsResult = MutableLiveData<ResultState<List<Point>>>()
    val myPointsResult: LiveData<ResultState<List<Point>>> = _myPointsResult

    private val _allPointsResult = MutableLiveData<ResultState<List<Point>>>()
    val allPointsResult: LiveData<ResultState<List<Point>>> = _allPointsResult

    /**
     * Método para agregar un nuevo punto con imagen
     */
    fun addPointWithImage(point: Point, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _addPointResult.postValue(ResultState.Loading)

                val uid = AuthManager().getCurrentUser()?.uid
                if (uid != null) {
                    point.userId = uid
                }

                val imageUrl = repository.uploadPointImage(imageUri, point.id ?: "")

                if (imageUrl != null) {
                    point.imagenUrl = imageUrl
                    val isSuccess = repository.addPoint(point)
                    _addPointResult.postValue(ResultState.Success(isSuccess))
                } else {
                    _addPointResult.postValue(ResultState.Error("Error al subir la imagen"))
                }
            } catch (e: Exception) {
                _addPointResult.postValue(ResultState.Error(e.message ?: "Error al agregar el punto"))
            }
        }
    }


    /**
     * Método para obtener los puntos del usuario actual
     */
    fun getMyPoints() {
        viewModelScope.launch {
            try {
                _myPointsResult.postValue(ResultState.Loading)
                val points = repository.getMyPoints()
                _myPointsResult.postValue(ResultState.Success(points))
            } catch (e: Exception) {
                _myPointsResult.postValue(ResultState.Error(e.message ?: "Error al cargar tus puntos"))
            }
        }
    }

    /**
     * Método para obtener todos los puntos
     */
    fun getAllPoints() {
        viewModelScope.launch {
            try {
                _allPointsResult.postValue(ResultState.Loading)
                val points = repository.getAllPoints()
                _allPointsResult.postValue(ResultState.Success(points))
            } catch (e: Exception) {
                _allPointsResult.postValue(ResultState.Error(e.message ?: "Error al cargar todos los puntos"))
            }
        }
    }

    /**
     * Devuelve el estado de favorito en tiempo real
     */
    fun favoriteStatus(pointId: String): LiveData<Boolean> {
        return liveData {
            val isFav = repository.checkIfFavorite(pointId)
            emit(isFav)
        }
    }

    /**
     * Marca un Point como favorito
     */
    fun addFavorite(pointId: String) {
        viewModelScope.launch {
            repository.addFavorite(pointId)
            loadFavorites()
        }
    }

    /**
     * Elimina un Point de los favoritos
     */
    fun removeFavorite(pointId: String) {
        viewModelScope.launch {
            repository.removeFavorite(pointId)
            loadFavorites()
        }
    }

    private val _favoritePointsResult = MutableLiveData<ResultState<List<Point>>>()
    val favoritePointsResult: LiveData<ResultState<List<Point>>> = _favoritePointsResult

    fun getFavoritePoints() {
        viewModelScope.launch {
            try {
                _favoritePointsResult.postValue(ResultState.Loading)
                val points = repository.getFavoritePoints()
                _favoritePointsResult.postValue(ResultState.Success(points))
            } catch (e: Exception) {
                _favoritePointsResult.postValue(ResultState.Error(e.message ?: "Error al cargar tus favoritos"))
            }
        }
    }

    private val _myAddedPointsResult = MutableLiveData<ResultState<List<Point>>>()
    val myAddedPointsResult: LiveData<ResultState<List<Point>>> = _myAddedPointsResult

    fun getMyAddedPoints() {
        viewModelScope.launch {
            try {
                _myAddedPointsResult.postValue(ResultState.Loading)
                val points = repository.getMyAddedPoints()
                _myAddedPointsResult.postValue(ResultState.Success(points))
            } catch (e: Exception) {
                _myAddedPointsResult.postValue(ResultState.Error(e.message ?: "Error al cargar tus aportes"))
            }
        }
    }

    private val _favoriteIds = MutableLiveData<List<String>>(emptyList())
    val favoriteIds: LiveData<List<String>> = _favoriteIds

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val ids = repository.getFavorites()
                _favoriteIds.postValue(ids)
            } catch (e: Exception) {
                _favoriteIds.postValue(emptyList())
            }
        }
    }




}


sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val message: String) : ResultState<Nothing>()
}

class AddPointViewModelFactory(
    private val repository: RepositoryUsuario
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddPointViewModel(repository) as T
    }
}

