package com.kevinchambi.shipmentmonitor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kevinchambi.shipmentmonitor.databinding.ActivityMainBinding
import com.kevinchambi.shipmentmonitor.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.findNavController()

        // If user is already logged in, navigate straight to map
        if (sessionManager.isLoggedIn()) {
            navController.navigate(R.id.action_loginFragment_to_mapFragment)
        }

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.forgotPasswordFragment -> {
                    binding.bottomNavigationView.visibility = android.view.View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = android.view.View.VISIBLE
                }
            }
        }
    }
}
