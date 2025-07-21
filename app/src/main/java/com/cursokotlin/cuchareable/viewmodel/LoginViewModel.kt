package com.cursokotlin.cuchareable.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cursokotlin.cuchareable.repository.ServicioInicioSesion

class LoginViewModel(private val servicio: ServicioInicioSesion) : ViewModel() {
    val loginResult = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()


    fun login(email: String, clave: String) {
        // Llamamos al servicio y manejamos el resultado con el callback
        servicio.iniciarSesion(email, clave) { success, error ->
            if (success) {
                loginResult.postValue(true) // Login exitoso
            } else {
                loginResult.postValue(false) // Login fallido
                errorMessage.postValue(error)
            }
        }
    }

}

class LoginViewModelFactory(private val servicio: ServicioInicioSesion) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(servicio) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}