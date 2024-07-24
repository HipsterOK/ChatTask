package ru.porcupine.chattask.ui.registration

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.porcupine.chattask.R
import ru.porcupine.chattask.databinding.FragmentRegistrationBinding
import ru.porcupine.chattask.util.SharedViewModel
import ru.porcupine.chattask.data.repository.UserRepository

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: RegistrationViewModel by viewModels {
        RegistrationViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedViewModel.phoneNumber.observe(viewLifecycleOwner) { phoneNumber ->
            binding.phoneNumberEditText.text = SpannableStringBuilder(phoneNumber)
        }

        sharedViewModel.countryCode.observe(viewLifecycleOwner) { countryCode ->
            binding.ccp.setCountryForNameCode(countryCode)
            binding.ccp.registerCarrierNumberEditText(binding.phoneNumberEditText)
        }

        binding.button.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val username = binding.usernameEditText.text.toString()
            val phoneNumber = binding.ccp.fullNumberWithPlus

            viewModel.registerUser(phoneNumber, name, username, {
                val userRepository = UserRepository(requireContext())
                userRepository.setShouldLoadFromServer(true)
                findNavController().navigate(R.id.action_registrationFragment_to_main_graph)
            }, { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                println(errorMessage)
            })
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
