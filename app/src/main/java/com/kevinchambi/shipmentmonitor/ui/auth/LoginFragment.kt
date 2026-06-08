package com.kevinchambi.shipmentmonitor.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                viewModel.login(email, password)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Ingresa tu usuario"
            return false
        }
        binding.tilEmail.error = null

        if (password.isEmpty()) {
            binding.tilPassword.error = "Ingresa tu contraseña"
            return false
        }
        binding.tilPassword.error = null

        return true
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    val loginData = it.getOrNull()
                    loginData?.let { data ->
                        sessionManager.saveToken(data.token_value, data.token_expired)
                        sessionManager.setKeepSession(binding.cbKeepSession.isChecked)
                        sessionManager.saveEmail(binding.etEmail.text.toString().trim())
                        navigateToMap()
                    }
                } else {
                    val error = it.exceptionOrNull()?.message ?: getString(R.string.error_generic)
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }
                viewModel.clearLoginResult()
            }
        }
    }

    private fun navigateToMap() {
        findNavController().navigate(R.id.action_loginFragment_to_mapFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
