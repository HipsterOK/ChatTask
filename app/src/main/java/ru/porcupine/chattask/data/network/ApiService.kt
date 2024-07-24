package ru.porcupine.chattask.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import ru.porcupine.chattask.data.model.AuthResponse
import ru.porcupine.chattask.data.model.CodeRequest
import ru.porcupine.chattask.data.model.PhoneRequest
import ru.porcupine.chattask.data.model.RefreshTokenResponse
import ru.porcupine.chattask.data.model.RegisterRequest
import ru.porcupine.chattask.data.model.RegisterResponse
import ru.porcupine.chattask.data.model.SendAuthCodeResponse
import ru.porcupine.chattask.data.model.UpdateProfileRequest
import ru.porcupine.chattask.data.model.UserProfileResponse

interface ApiService {
    @POST("/api/v1/users/send-auth-code/")
    suspend fun sendAuthCode(@Body request: PhoneRequest): Response<SendAuthCodeResponse>

    @POST("/api/v1/users/check-auth-code/")
    suspend fun checkAuthCode(@Body request: CodeRequest): Response<AuthResponse>

    @POST("/api/v1/users/register/")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/api/v1/users/refresh-token/")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Response<RefreshTokenResponse>

    @GET("/api/v1/users/me/")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PUT("/api/v1/users/me/")
    suspend fun updateUserProfile(@Body requestBody: UpdateProfileRequest): Response<UserProfileResponse>
}
