package com.cursokotlin.cuchareable.repository

import com.cursokotlin.cuchareable.AuthManager

class ServicioInicioSesion {
    private val authManager = AuthManager()

    fun iniciarSesion(
        email: String,
        clave: String,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        authManager.signInUser(email, clave) { success, user, error ->
            if (success && user != null) {
                onResult(true, null)
            } else {
                val errorMsg = when (error) {
                    "ERROR_USER_NOT_FOUND" -> "No existe un usuario con ese correo"
                    "ERROR_WRONG_PASSWORD" -> "La contraseña es incorrecta"
                    else -> error ?: "Error desconocido"
                }
                onResult(false, errorMsg)
            }
        }
    }

    fun cerrarSesion(){
        authManager.logout()
    }

}