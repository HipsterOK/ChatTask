package ru.porcupine.chattask.ui.userinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.porcupine.chattask.R
import ru.porcupine.chattask.data.model.UserProfile
import ru.porcupine.chattask.data.repository.UserRepository
import ru.porcupine.chattask.databinding.FragmentUserInfoBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class UserInfoFragment : Fragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = UserRepository(requireContext())
        val factory = UserInfoViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, factory)[UserInfoViewModel::class.java]

        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let { updateUI(it) }
        }

        if (userRepository.shouldLoadFromServer()) {
            viewModel.fetchUserProfile()
        } else {
            val cachedProfile = userRepository.getCachedUserProfile()
            if (cachedProfile != null) {
                updateUI(cachedProfile)
            }
        }

        binding.edit.setOnClickListener {
            findNavController().navigate(R.id.action_userInfoFragment_to_editProfileFragment)
        }
    }

    private fun updateUI(userProfile: UserProfile) {
        binding.apply {
            userProfile.avatars?.bigAvatar?.let { avatarUriString ->
                val avatarUrl = "https://plannerok.ru/$avatarUriString"
                Glide.with(this@UserInfoFragment)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(userAvatar)
            }

            userName.text = userProfile.name ?: "-"
            userUsername.text = userProfile.username ?: "-"
            userPhone.text = userProfile.phone ?: "-"
            userCity.text = userProfile.city ?: "-"
            userBirthday.text = userProfile.birthday ?: "-"
            userStatus.text = userProfile.status ?: "-"
            userZodiac.text = getZodiacSign(userProfile.birthday)
        }
    }


    private fun getZodiacSign(birthday: String?): String {
        if (birthday.isNullOrEmpty()) {
            return "-"
        }

        return try {
            val date = LocalDate.parse(birthday, DateTimeFormatter.ISO_LOCAL_DATE)
            val month = date.monthValue
            val day = date.dayOfMonth

            when {
                (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Водолей"
                (month == 2 && day >= 19) || (month == 3 && day <= 20) -> "Рыбы"
                (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Овен"
                (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Телец"
                (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Близнецы"
                (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Рак"
                (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Лев"
                (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Дева"
                (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Весы"
                (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Скорпион"
                (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "Стрелец"
                else -> "Козерог"
            }
        } catch (e: Exception) {
            "-"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
