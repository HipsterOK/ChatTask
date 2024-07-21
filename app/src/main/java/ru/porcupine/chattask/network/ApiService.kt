package ru.porcupine.chattask.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class PhoneRequest(val phone: String)
data class CodeRequest(val phone: String, val code: String)
data class RegisterRequest(val phone: String, val name: String, val username: String)
data class RegisterResponse(val refreshToken: String, val accessToken: String, val userId: String)

interface ApiService {
    @POST("/api/v1/users/send-auth-code/")
    suspend fun sendAuthCode(@Body request: PhoneRequest): Response<SendAuthCodeResponse>

    @POST("/api/v1/users/check-auth-code/")
    suspend fun checkAuthCode(@Body request: CodeRequest): Response<AuthResponse>

    @POST("/api/v1/users/register/")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>
}
