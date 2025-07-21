package com.cursokotlin.cuchareable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursokotlin.cuchareable.model.Point
import com.cursokotlin.cuchareable.recycler.PointsAdapter
import com.cursokotlin.cuchareable.repository.RepositoryUsuario
import com.cursokotlin.cuchareable.viewmodel.AddPointViewModel
import com.cursokotlin.cuchareable.viewmodel.AddPointViewModelFactory
import com.cursokotlin.cuchareable.viewmodel.ResultState

class AllFragment : Fragment() {

    private lateinit var viewModel: AddPointViewModel
    private lateinit var adapter: PointsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var sectionType: PointsAdapter.SectionType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sectionType = arguments?.getSerializable(ARG_SECTION_TYPE) as PointsAdapter.SectionType
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_all, container, false)

        val repository = RepositoryUsuario()
        val factory = AddPointViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AddPointViewModel::class.java]

        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerViewAll)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PointsAdapter(
            emptyList(),
            sectionType,
            viewModel,
            viewLifecycleOwner
        )
        recyclerView.adapter = adapter

        viewModel.loadFavorites()

        when (sectionType) {
            PointsAdapter.SectionType.ALL_POINTS -> {
                viewModel.allPointsResult.observe(viewLifecycleOwner) { result ->
                    handleResult(result, PointsAdapter.SectionType.ALL_POINTS)
                }
                viewModel.getAllPoints()
            }
            PointsAdapter.SectionType.FAVORITE_POINTS -> {
                viewModel.favoritePointsResult.observe(viewLifecycleOwner) { result ->
                    handleResult(result, PointsAdapter.SectionType.FAVORITE_POINTS)
                }
                viewModel.getFavoritePoints()
            }
            PointsAdapter.SectionType.MY_ADDED_POINTS -> {
                viewModel.myAddedPointsResult.observe(viewLifecycleOwner) { result ->
                    handleResult(result, PointsAdapter.SectionType.MY_ADDED_POINTS)
                }
                viewModel.getMyAddedPoints()
            }
        }

        return view
    }

    private fun handleResult(
        result: ResultState<List<Point>>,
        type: PointsAdapter.SectionType
    ) {
        when (result) {
            is ResultState.Loading -> {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            is ResultState.Success -> {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.updateData(result.data, type)
            }
            is ResultState.Error -> {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val ARG_SECTION_TYPE = "section_type"

        fun newInstance(sectionType: PointsAdapter.SectionType): AllFragment {
            val fragment = AllFragment()
            val args = Bundle()
            args.putSerializable(ARG_SECTION_TYPE, sectionType)
            fragment.arguments = args
            return fragment
        }
    }
}

