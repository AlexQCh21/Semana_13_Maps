package com.cursokotlin.s12_mvvm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cursokotlin.s12_mvvm.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    lateinit var authManager: AuthManager
    lateinit var binding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        authManager = AuthManager()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.logOut.setOnClickListener {
            cerrarSesion()
        }

        if(authManager.getCurrentUser()==null){
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun cerrarSesion() {
        authManager.logout()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }



}