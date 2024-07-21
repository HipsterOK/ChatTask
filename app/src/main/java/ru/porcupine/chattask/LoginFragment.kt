package ru.porcupine.chattask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.porcupine.chattask.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST") return LoginViewModel(CurrentRegionUseCase()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.currentRegion.observe(viewLifecycleOwner) { region ->
            binding.ccp.setDefaultCountryUsingNameCode(region)
            binding.ccp.registerCarrierNumberEditText(binding.phoneNumberEditText)
        }

        viewModel.codeVisibility.observe(viewLifecycleOwner) { visibility ->
            if (visibility) {
                binding.codeEditText.visibility = View.VISIBLE
                binding.notRegisterText.visibility = View.VISIBLE
            } else {
                binding.codeEditText.visibility = View.GONE
                binding.notRegisterText.visibility = View.GONE
            }
        }

        binding.notRegisterText.setOnClickListener {
            sharedViewModel.setPhoneNumber(binding.phoneNumberEditText.text.toString())
            sharedViewModel.setCountryCode(binding.ccp.selectedCountryNameCode)
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        binding.button.setOnClickListener {
            if (binding.phoneNumberEditText.text.isNotBlank()) {
                val phoneNumber =
                    binding.ccp.selectedCountryCodeWithPlus + binding.phoneNumberEditText.text.toString()
                val code = binding.codeEditText.text.toString().takeIf { it.isNotBlank() }
                viewModel.onClickLoginBtn(requireContext(), phoneNumber, code)
            } else {
                Toast.makeText(
                    requireActivity(), "Введите корректный номер телефона!", Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
