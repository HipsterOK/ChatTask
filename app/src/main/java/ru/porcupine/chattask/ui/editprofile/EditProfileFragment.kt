package ru.porcupine.chattask.ui.editprofile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.porcupine.chattask.Application
import ru.porcupine.chattask.R
import ru.porcupine.chattask.databinding.FragmentEditProfileBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels {
        EditProfileViewModelFactory(
            (requireActivity().application as Application).userRepository, requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.profileData.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.editProfileName.setText(it.name)
                binding.editProfileUserName.setText(it.username)
                binding.editProfileBirthday.setText(it.birthday)
                binding.editProfileCity.setText(it.city)
                binding.editProfileVk.setText(it.vk)
                binding.editProfileInstagram.setText(it.instagram)
                binding.editProfileStatus.setText(it.status)

                it.avatars?.bigAvatar?.let { avatarUriString ->
                    val avatarUrl = "https://plannerok.ru/$avatarUriString"
                    Glide.with(this@EditProfileFragment).load(avatarUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(android.R.drawable.ic_popup_sync).into(binding.editProfileAvatar)
                }
            }
        }

        binding.editProfileChangeAvatarButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.editProfileSaveButton.setOnClickListener {
            saveProfileChanges()
        }

        binding.editProfileBirthday.setOnClickListener {
            showDatePicker(binding.editProfileBirthday)
        }

        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                findNavController().popBackStack()
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "Ошибка: ${exception.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    private fun saveProfileChanges() {
        val username = binding.editProfileUserName.text.toString()
        val name = binding.editProfileName.text.toString()
        val birthday = binding.editProfileBirthday.text.toString()
        val city = binding.editProfileCity.text.toString()
        val vk = binding.editProfileVk.text.toString()
        val instagram = binding.editProfileInstagram.text.toString()
        val status = binding.editProfileStatus.text.toString()
        viewModel.saveProfileChanges(username, name, birthday, city, vk, instagram, status)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                viewModel.updateAvatar(uri)
                binding.editProfileAvatar.setImageURI(uri)
            }
        }
    }

    private fun showDatePicker(view: View) {
        if (view is EditText) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    view.setText(sdf.format(selectedDate.time))
                }, year, month, day
            )
            datePickerDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}
