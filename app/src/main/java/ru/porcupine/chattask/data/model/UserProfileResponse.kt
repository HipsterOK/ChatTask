package ru.porcupine.chattask.data.model

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("profile_data") val profileData: UserProfile
)