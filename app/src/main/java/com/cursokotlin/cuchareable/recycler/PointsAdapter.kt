package com.cursokotlin.cuchareable.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cursokotlin.cuchareable.MapaFragment
import com.cursokotlin.cuchareable.R
import com.cursokotlin.cuchareable.model.Point
import com.cursokotlin.cuchareable.viewmodel.AddPointViewModel
import com.google.android.material.button.MaterialButton

class PointsAdapter(
    private var points: List<Point>,
    private var sectionType: SectionType,
    private val viewModel: AddPointViewModel,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<PointsAdapter.PointViewHolder>() {

    enum class SectionType {
        ALL_POINTS, FAVORITE_POINTS, MY_ADDED_POINTS
    }

    inner class PointViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgLocal: ImageView = view.findViewById(R.id.local_img)
        val txtNombre: TextView = view.findViewById(R.id.txtNombre)
        val txtReferencia: TextView = view.findViewById(R.id.txtReferencia)
        val txtTipo: TextView = view.findViewById(R.id.txtTipo)
        val btnFavorite: MaterialButton = view.findViewById(R.id.btnFavorite)
        val btnMapa: MaterialButton = view.findViewById(R.id.buttonWithIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_point, parent, false)
        return PointViewHolder(view)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        val point = points[position]

        // Cargar imagen
        Glide.with(holder.itemView.context)
            .load(point.imagenUrl)
            .placeholder(R.drawable.photo_placeholder)
            .into(holder.imgLocal)

        // Llenar datos
        holder.txtNombre.text = point.nombre
        holder.txtReferencia.text = point.referencia
        holder.txtTipo.text = point.tipo

        // Comportamiento por sección
        when (sectionType) {
            SectionType.ALL_POINTS -> {
                // Observa estado favorito
                val isFavorite = viewModel.favoriteIds.value?.contains(point.id) == true
                holder.btnFavorite.isChecked = isFavorite


                holder.btnFavorite.setOnClickListener {
                    if (holder.btnFavorite.isChecked) {
                        viewModel.addFavorite(point.id!!)
                    } else {
                        viewModel.removeFavorite(point.id!!)
                    }
                }
            }
            SectionType.FAVORITE_POINTS -> {
                holder.btnFavorite.isChecked = true
                holder.btnFavorite.setOnClickListener {
                    viewModel.removeFavorite(point.id!!)
                }
            }
            SectionType.MY_ADDED_POINTS -> {
                holder.btnFavorite.visibility = View.GONE // Oculta el botón favoritos
            }
        }



        holder.btnMapa.setOnClickListener {
            val bundle = Bundle().apply {
                putDouble("latitud", point.latitud)
                putDouble("longitud", point.longitud)
            }
            holder.itemView.findNavController().navigate(R.id.mapaFragment, bundle)
        }



    }

    override fun getItemCount(): Int = points.size

    // Método para actualizar lista y sección
    fun updateData(newPoints: List<Point>, newSectionType: SectionType) {
        points = newPoints
        sectionType = newSectionType
        notifyDataSetChanged()
    }
}
