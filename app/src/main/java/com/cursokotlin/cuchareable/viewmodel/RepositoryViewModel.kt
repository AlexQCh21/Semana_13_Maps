package com.cursokotlin.cuchareable.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cursokotlin.cuchareable.model.Usuario
import com.cursokotlin.cuchareable.repository.RepositoryUsuario
import kotlinx.coroutines.launch

class RepositoryViewModel(private val repository: RepositoryUsuario) : ViewModel() {

    val registroResult = MutableLiveData<Boolean>()
    val deleteResult = MutableLiveData<Boolean>()

    fun registrar(usuario: Usuario, clave: String) {
        viewModelScope.launch {
            val result = repository.add(usuario, clave)
            registroResult.postValue(result)
        }
    }

    fun deleteCurrentUser() {
        viewModelScope.launch {
            val result = repository.deleteCurrentUser()
            deleteResult.postValue(result)
        }
    }
}

class RepositoryViewModelFactory(private val repository: RepositoryUsuario):ViewModelProvider.Factory{

    override fun <T:ViewModel> create(modelClass:Class<T>):T{
        if (modelClass.isAssignableFrom(RepositoryViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return RepositoryViewModel(repository) as T
        }

        throw IllegalArgumentException("Clase de View Model desconocida")
    }
}
