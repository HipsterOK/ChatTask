package ru.porcupine.chattask.network

data class AuthResponse(
    val refreshToken: String,
    val accessToken: String,
    val userId: String,
    val isUserExists: Boolean
)
