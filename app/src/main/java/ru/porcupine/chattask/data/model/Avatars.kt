package ru.porcupine.chattask.data.model

import com.google.gson.annotations.SerializedName

data class Avatars(
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("bigAvatar") val bigAvatar: String?,
    @SerializedName("miniAvatar") val miniAvatar: String?
)