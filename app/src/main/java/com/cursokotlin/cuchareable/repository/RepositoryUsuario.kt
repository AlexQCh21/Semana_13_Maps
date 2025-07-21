package com.cursokotlin.cuchareable.repository

import android.net.Uri
import android.util.Log
import com.cursokotlin.cuchareable.AuthManager
import com.cursokotlin.cuchareable.model.Point
import com.cursokotlin.cuchareable.model.Usuario

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

    suspend fun addPoint(point: Point): Boolean {
        return try {
            val isSuccess = firestoreManager.addPoint(point)
            Log.d("RepositoryUsuario", "Punto guardado: $isSuccess")
            isSuccess
        } catch (e: Exception) {
            Log.e("RepositoryUsuario", "Error en addPoint()", e)
            false
        }
    }

    suspend fun getMyPoints(): List<Point> {
        return try {
            val points = firestoreManager.getMyPoints()
            Log.d("RepositoryUsuario", "Puntos del usuario: ${points.size}")
            points
        } catch (e: Exception) {
            Log.e("RepositoryUsuario", "Error en getMyPoints()", e)
            emptyList()
        }
    }

    suspend fun getAllPoints(): List<Point> {
        return try {
            val points = firestoreManager.getAllPoints()
            Log.d("RepositoryUsuario", "Total de puntos: ${points.size}")
            points
        } catch (e: Exception) {
            Log.e("RepositoryUsuario", "Error en getAllPoints()", e)
            emptyList()
        }
    }



    suspend fun deleteCurrentUser(): Boolean {
        return try {
            authManager.deleteCurrentUserSuspend()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun uploadPointImage(uri: Uri, pointId: String): String? {
        val storageManager = StorageManager()
        return storageManager.uploadPointImage(uri, pointId)
    }

    /**
     * Marcar como favorito
     */
    suspend fun addFavorite(pointId: String): Boolean {
        return firestoreManager.addFavorite(pointId)
    }

    /**
     * Quitar de favoritos
     */
    suspend fun removeFavorite(pointId: String): Boolean {
        return firestoreManager.removeFavorite(pointId)
    }

    /**
     * Verificar si un Point es favorito
     */
    suspend fun checkIfFavorite(pointId: String): Boolean {
        return firestoreManager.isFavorite(pointId)
    }

    /**
     * Obtener IDs de favoritos
     */
    suspend fun getFavorites(): List<String> {
        return firestoreManager.getFavorites()
    }

    suspend fun getFavoritePoints(): List<Point> {
        return try {
            val favoriteIds = firestoreManager.getFavorites()
            if (favoriteIds.isNotEmpty()) {
                // Trae todos los puntos y filtra los favoritos
                val allPoints = firestoreManager.getAllPoints()
                val favoritePoints = allPoints.filter { it.id in favoriteIds }
                Log.d("RepositoryUsuario", "Puntos favoritos: ${favoritePoints.size}")
                favoritePoints
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("RepositoryUsuario", "Error en getFavoritePoints()", e)
            emptyList()
        }
    }

    suspend fun getMyAddedPoints(): List<Point> {
        return try {
            val userId = authManager.getCurrentUser()?.uid
            val points = firestoreManager.getAllPoints()
                .filter { it.userId == userId }
            points
        } catch (e: Exception) {
            Log.e("RepositoryUsuario", "Error en getMyAddedPoints()", e)
            emptyList()
        }
    }



}
