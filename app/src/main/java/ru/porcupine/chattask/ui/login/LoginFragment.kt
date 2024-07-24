package ru.porcupine.chattask.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.porcupine.chattask.databinding.FragmentLoginBinding
import ru.porcupine.chattask.domain.usecase.CurrentRegionUseCase
import ru.porcupine.chattask.util.SharedViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            requireActivity().application, CurrentRegionUseCase()
        )
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
                binding.phoneNumberEditText.isEnabled = false
                binding.ccp.setCcpClickable(false)
            } else {
                binding.codeEditText.visibility = View.GONE
                binding.notRegisterText.visibility = View.GONE
                binding.phoneNumberEditText.isEnabled = true
                binding.ccp.setCcpClickable(true)
            }
        }

        viewModel.navigateDict.observe(viewLifecycleOwner) { dir ->
            if (dir != 0) {
                findNavController().navigate(dir)
                viewModel.resetDictionary()
            }
        }

        binding.notRegisterText.setOnClickListener {
            sharedViewModel.setPhoneNumber(binding.phoneNumberEditText.text.toString())
            sharedViewModel.setCountryCode(binding.ccp.selectedCountryNameCode)
            viewModel.goToRegister()
        }

        binding.button.setOnClickListener {
            if (binding.phoneNumberEditText.text.isNotBlank()) {
                val phoneNumber = binding.ccp.fullNumberWithPlus
                val code = binding.codeEditText.text.toString().takeIf { it.isNotBlank() }
                sharedViewModel.setPhoneNumber(binding.phoneNumberEditText.text.toString())
                sharedViewModel.setCountryCode(binding.ccp.selectedCountryNameCode)
                viewModel.onClickLoginBtn(phoneNumber, code)
            } else {
                Toast.makeText(
                    requireActivity(), "Введите корректный номер телефона!", Toast.LENGTH_SHORT
                ).show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.codeVisibility.value == true) viewModel.backToPhone()
                    else requireActivity().finish()
                }
            })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
