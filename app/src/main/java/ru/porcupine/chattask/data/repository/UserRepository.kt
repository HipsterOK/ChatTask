package ru.porcupine.chattask.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import ru.porcupine.chattask.data.model.AvatarData
import ru.porcupine.chattask.data.model.UpdateProfileRequest
import ru.porcupine.chattask.data.model.UserProfile
import ru.porcupine.chattask.data.model.UserProfileResponse
import ru.porcupine.chattask.data.network.RetrofitInstance

class UserRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun setShouldLoadFromServer(flag: Boolean) {
        prefs.edit().putBoolean("should_load_from_server", flag).apply()
    }

    fun setLastUserID(userId:Int){
        prefs.edit().putInt("last_user_id", userId).apply()
    }

    fun lastUserID(): Int {
        return prefs.getInt("last_user_id", -1)
    }

    suspend fun getUserProfile(): UserProfile? {
        val shouldLoadFromServer = shouldLoadFromServer()
        if (shouldLoadFromServer) {
            return withContext(Dispatchers.IO) {
                val response: Response<UserProfileResponse> = RetrofitInstance.api.getUserProfile()
                if (response.isSuccessful) {
                    response.body()?.profileData?.let {
                        cacheUserProfile(it)
                        setShouldLoadFromServer(false)
                        return@withContext it
                    }
                }
                null
            }
        } else {
            return getCachedUserProfile()
        }
    }

    fun getCachedUserProfile(): UserProfile? {
        val json = prefs.getString("user_profile", null)
        return json?.let { gson.fromJson(it, UserProfile::class.java) }
    }

    private fun cacheUserProfile(userProfile: UserProfile) {
        prefs.edit().putString("user_profile", gson.toJson(userProfile)).apply()
    }

    suspend fun updateProfile(
        name: String,
        username: String,
        birthday: String,
        city: String,
        vk: String,
        instagram: String,
        status: String,
        avatar: AvatarData?
    ) {
        val request = UpdateProfileRequest(
            name = name,
            username = username,
            birthday = birthday,
            city = city,
            vk = vk,
            instagram = instagram,
            status = status,
            avatar = avatar
        )

        withContext(Dispatchers.IO) {
            val response: Response<UserProfileResponse> =
                RetrofitInstance.api.updateUserProfile(request)
            if (response.isSuccessful) {
                response.body()?.profileData?.let {
                    cacheUserProfile(it)
                    setShouldLoadFromServer(false)
                }
            } else {
                Log.e("UserRepository", "Failed to update profile: ${response.errorBody()}")
            }
        }
    }

    fun shouldLoadFromServer(): Boolean {
        val flag = prefs.getBoolean("should_load_from_server", true)
        Log.d("UserRepository", "shouldLoadFromServer: $flag")
        return flag
    }
}