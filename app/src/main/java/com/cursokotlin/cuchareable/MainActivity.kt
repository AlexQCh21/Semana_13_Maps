package com.cursokotlin.cuchareable

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.cursokotlin.cuchareable.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    lateinit var autnManager:AuthManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        autnManager = AuthManager()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        // Forzar color de la barra y texto blanco
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = 0 // texto blanco
        }


        // Obtenemos el NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Cambiar startDestination dinámicamente
        val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)
        if (autnManager.getCurrentUser() != null) {
            navGraph.setStartDestination(R.id.homeFragment) // Usuario autenticado
        } else {
            navGraph.setStartDestination(R.id.loginFragment) // No autenticado
        }
        navController.graph = navGraph
    }

}