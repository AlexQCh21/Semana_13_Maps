package com.cursokotlin.cuchareable.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageManager {

    private val storage = FirebaseStorage.getInstance()

    companion object {
        const val TAG = "StorageManager"
        const val FOLDER_POINTS_IMAGES = "points_images"
    }

    /**
     * Sube la imagen al Storage y devuelve la URL pública
     */
    suspend fun uploadPointImage(uri: Uri, pointId: String): String? {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid.isNullOrEmpty()) {
                Log.e(TAG, "Usuario no autenticado, no se puede subir imagen")
                return null
            }

            // Ruta donde se guardará la imagen en Storage
            val imageRef = storage.reference
                .child("$FOLDER_POINTS_IMAGES/$uid/${pointId}.jpg")

            // Subir el archivo
            imageRef.putFile(uri).await()

            // Obtener la URL de descarga
            val downloadUrl = imageRef.downloadUrl.await().toString()
            Log.d(TAG, "Imagen subida correctamente: $downloadUrl")

            downloadUrl
        } catch (e: Exception) {
            Log.e(TAG, "Error al subir imagen", e)
            null
        }
    }

    /**
     * Borra una imagen del Storage (opcional)
     */
    suspend fun deletePointImage(pointId: String): Boolean {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid.isNullOrEmpty()) {
                Log.e(TAG, "Usuario no autenticado, no se puede borrar imagen")
                return false
            }

            val imageRef = storage.reference
                .child("$FOLDER_POINTS_IMAGES/$uid/${pointId}.jpg")

            imageRef.delete().await()
            Log.d(TAG, "Imagen eliminada correctamente: $pointId.jpg")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al borrar imagen", e)
            false
        }
    }
}
