package com.cursokotlin.cuchareable

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.cursokotlin.cuchareable.model.Point
import com.cursokotlin.cuchareable.repository.RepositoryUsuario
import com.cursokotlin.cuchareable.viewmodel.AddPointViewModel
import com.cursokotlin.cuchareable.viewmodel.AddPointViewModelFactory
import com.cursokotlin.cuchareable.viewmodel.ResultState
import com.google.android.material.button.MaterialButton
import java.io.File

class FormularioDialogFragment : DialogFragment() {

    private lateinit var viewModel: AddPointViewModel
    private var selectedImageUri: Uri? = null // Para guardar la URI de la imagen
    lateinit var imgPhoto:ImageView

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imgPhoto.setImageURI(it)
        }
    }

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            // Convierte el Bitmap a URI temporal
            val uri = saveBitmapToCacheAndGetUri(it)
            selectedImageUri = uri
            imgPhoto.setImageBitmap(bitmap) // Muestra la foto tomada
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = RepositoryUsuario()
        viewModel = ViewModelProvider(
            requireActivity(),
            AddPointViewModelFactory(repository)
        )[AddPointViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_point, null)

        val etName = view.findViewById<EditText>(R.id.etName)
        val etReference = view.findViewById<EditText>(R.id.etReference)
        val etType = view.findViewById<EditText>(R.id.etType)
        val etLongitude = view.findViewById<EditText>(R.id.etLongitude)
        val etLatitude = view.findViewById<EditText>(R.id.etLatitude)
        val btnAddPoint = view.findViewById<MaterialButton>(R.id.btnAddPoint)
        val btnAddImgLocal = view.findViewById<MaterialButton>(R.id.btnAddImgLocal)
        imgPhoto = view.findViewById<ImageView>(R.id.imgPhoto)

        // Seleccionar o tomar una foto
        btnAddImgLocal.setOnClickListener {
            showImageOptions()
        }

        // Agregar Point con imagen
        btnAddPoint.setOnClickListener {
            val name = etName.text.toString().trim()
            val reference = etReference.text.toString().trim()
            val type = etType.text.toString().trim()
            val longitude = etLongitude.text.toString().toDoubleOrNull()
            val latitude = etLatitude.text.toString().toDoubleOrNull()

            if (name.isEmpty() || reference.isEmpty() || type.isEmpty() ||
                longitude == null || latitude == null || selectedImageUri == null
            ) {
                Toast.makeText(requireContext(), "Completa todos los campos e incluye una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val point = Point(
                nombre = name,
                referencia = reference,
                tipo = type,
                longitud = longitude,
                latitud = latitude
            )

            viewModel.addPointWithImage(point, selectedImageUri!!)
        }

        // Observa el resultado
        viewModel.addPointResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    btnAddPoint.isEnabled = false
                    btnAddPoint.text = "Agregando..."
                }
                is ResultState.Success -> {
                    btnAddPoint.isEnabled = true
                    btnAddPoint.text = "Agregar Point"
                    if (result.data) {
                        Toast.makeText(requireContext(), "Point agregado correctamente", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Error al agregar el Point", Toast.LENGTH_SHORT).show()
                    }
                }
                is ResultState.Error -> {
                    btnAddPoint.isEnabled = true
                    btnAddPoint.text = "Agregar Point"
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.setView(view)
        return builder.create()
    }

    // Muestra opciones para elegir o tomar foto
    private fun showImageOptions() {
        val options = arrayOf("Elegir de la galería", "Tomar foto")
        AlertDialog.Builder(requireContext())
            .setTitle("Selecciona una opción")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> pickImageLauncher.launch("image/*")
                    1 -> takePhotoLauncher.launch(null)
                }
            }
            .show()
    }

    // Convierte Bitmap a URI temporal
    private fun saveBitmapToCacheAndGetUri(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
    }
}

