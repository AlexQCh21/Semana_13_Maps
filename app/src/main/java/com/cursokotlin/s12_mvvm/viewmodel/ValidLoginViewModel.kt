package com.cursokotlin.s12_mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ValidLoginViewModel:ViewModel() {
    val email = MutableLiveData<String>()
    val clave = MutableLiveData<String>()

    val emailError = MutableLiveData<String>()
    val claveError = MutableLiveData<String?>()

    fun validateEmail(newEmail:String){
        email.value=newEmail
        emailError.value = if (isValidEmail(newEmail)) null else "Correo inválido"
    }

    fun validateClave(newClave:String){
        clave.value=newClave
        claveError.value = isValidPassword(newClave)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(clave: String): String? {
        // Al menos 1 mayúscula
        val hasUppercase = clave.any { it.isUpperCase() }
        if (!hasUppercase){
            return "Debe de tener al menos una mayúscula"
        }

        // Al menos 1 número
        val hasDigit = clave.any { it.isDigit() }
        if (!hasDigit){
            return "Debe de tener al menos un número"
        }

        // Al menos 1 carácter especial (#, @, *, etc.)
        val hasSpecialChar = clave.any { "!@#\$%^&*()_+-=[]{}|;':,.<>/?".contains(it) }
        if (!hasSpecialChar){
            return "Debe de tener al menos un caracter especial"
        }

        // Longitud mínima de 8
        val hasMinLength = clave.length >= 8
        if (!hasMinLength){
            return "Debe de ser mínimo de 8 caracteres"
        }

        return null
    }
}