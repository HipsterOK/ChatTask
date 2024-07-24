package ru.porcupine.chattask.data.model

data class RefreshTokenResponse(
    val refreshToken: String,
    val accessToken: String,
    val userId: Int
)
