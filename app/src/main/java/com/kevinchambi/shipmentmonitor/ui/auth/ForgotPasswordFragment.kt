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

        setupObservers()
        setupClickListeners()
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
        }

        viewModel.forgotResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.forgot_success),
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigateUp()
                } else {
                    val error = it.exceptionOrNull()?.message ?: getString(R.string.error_generic)
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }
                viewModel.clearForgotResult()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
