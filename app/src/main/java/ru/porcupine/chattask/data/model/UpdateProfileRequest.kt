package ru.porcupine.chattask.data.model

data class UpdateProfileRequest(
    val name: String,
    val username: String,
    val birthday: String,
    val city: String,
    val vk: String,
    val instagram: String,
    val status: String,
    val avatar: AvatarData?
)
