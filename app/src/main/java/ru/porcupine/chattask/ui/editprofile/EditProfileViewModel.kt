package ru.porcupine.chattask.ui.editprofile

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.porcupine.chattask.data.model.AvatarData
import ru.porcupine.chattask.data.model.UserProfile
import ru.porcupine.chattask.data.repository.UserRepository
import java.io.ByteArrayOutputStream

class EditProfileViewModel(
    private val userRepository: UserRepository, private val context: Context
) : ViewModel() {

    private val _profileData = MutableLiveData<UserProfile>()
    val profileData: LiveData<UserProfile> get() = _profileData

    private val _avatar = MutableLiveData<AvatarData>()
    val avatar: LiveData<AvatarData> get() = _avatar

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> get() = _saveResult

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            val profile = userRepository.getUserProfile()
            _profileData.value = profile!!
        }
    }

    fun saveProfileChanges(
        username: String,
        name: String,
        birthday: String,
        city: String,
        vk: String,
        instagram: String,
        status: String
    ) {
        viewModelScope.launch {
            try {
                val avatarData = _avatar.value
                userRepository.updateProfile(
                    name = name,
                    username = username,
                    birthday = birthday,
                    city = city,
                    vk = vk,
                    instagram = instagram,
                    status = status,
                    avatar = avatarData
                )
                _saveResult.value = Result.success(Unit)
                userRepository.setShouldLoadFromServer(true)
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            }
        }
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            val fileName = getFileNameFromUri(uri)
            val base64 = convertImageToBase64(uri)
            _avatar.value = AvatarData(fileName, base64)
        }
    }

    @SuppressLint("Range")
    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = "avatar.jpg"
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME)) ?: "avatar.jpg"
            }
        }
        return fileName
    }

    private fun convertImageToBase64(uri: Uri): String {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}