package com.cursokotlin.s12_mvvm.repository

import android.util.Log
import com.cursokotlin.s12_mvvm.AuthManager
import com.cursokotlin.s12_mvvm.model.Usuario

class RepositoryUsuario {

    private val firestoreManager = FirestoreManager()
    private val authManager = AuthManager()

    suspend fun add(usuario: Usuario, clave: String): Boolean {
        return try {
            val isAuthSuccess = authManager.signUpUserSuspend(usuario.email ?: "", clave)
            Log.d("RepositoryUsuario", "Resultado de Auth: $isAuthSuccess")

            if (isAuthSuccess) {
                firestoreManager.saveUserSuspend(usuario)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteCurrentUser(): Boolean {
        return try {
            authManager.deleteCurrentUserSuspend()
        } catch (e: Exception) {
            false
        }
    }
}
