package com.kevinchambi.shipmentmonitor.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kevinchambi.shipmentmonitor.R
import com.kevinchambi.shipmentmonitor.databinding.FragmentLoginBinding
import com.kevinchambi.shipmentmonitor.utils.SessionManager

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        sessionManager = SessionManager(requireContext())

        if (sessionManager.isLoggedIn()) {
            navigateToMap()
            return
        }

        setupTextWatchers()
        setupClickListeners()
        setupObservers()
    }

    private fun setupTextWatchers() {
        binding.etEmail.addTextChangedListener {
            binding.tilEmail.error = null
        }
        binding.etPassword.addTextChangedListener {
            binding.tilPassword.error = null
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateForm(email, password)) {
                viewModel.login(email, password)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = "Ingresa tu usuario"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Ingresa tu contraseña"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
            binding.etEmail.isEnabled = !isLoading
            binding.etPassword.isEnabled = !isLoading
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    val loginData = it.getOrNull()
                    if (loginData != null) {
                        sessionManager.saveToken(loginData.token_value, loginData.token_expired)
                        sessionManager.setKeepSession(binding.cbKeepSession.isChecked)
                        sessionManager.saveEmail(binding.etEmail.text.toString().trim())
                        navigateToMap()
                    }
                } else {
                    val errorMessage = it.exceptionOrNull()?.message ?: getString(R.string.error_generic)
                    showSnackbar(errorMessage)
                }
                viewModel.clearLoginResult()
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToMap() {
        findNavController().navigate(R.id.action_loginFragment_to_mapFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
