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
import com.kevinchambi.shipmentmonitor.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupTextWatchers()
        setupClickListeners()
        setupObservers()
    }

    private fun setupTextWatchers() {
        binding.etUser.addTextChangedListener {
            binding.tilUser.error = null
        }
    }

    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            val user = binding.etUser.text.toString().trim()
            if (user.isEmpty()) {
                binding.tilUser.error = "Ingresa tu usuario"
                return@setOnClickListener
            }
            binding.tilUser.error = null
            viewModel.forgotPassword(user)
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSend.isEnabled = !isLoading
            binding.btnCancel.isEnabled = !isLoading
            binding.etUser.isEnabled = !isLoading
        }

        viewModel.forgotResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    val message = it.getOrNull() ?: getString(R.string.forgot_success)
                    showSnackbar(message)
                    findNavController().navigateUp()
                } else {
                    val errorMessage = it.exceptionOrNull()?.message ?: getString(R.string.error_generic)
                    showSnackbar(errorMessage)
                }
                viewModel.clearForgotResult()
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
