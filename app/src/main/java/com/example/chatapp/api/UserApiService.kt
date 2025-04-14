package com.example.chatapp.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApiService {
    @Multipart
    @POST("upload")
    suspend fun uploadProfile(
        @Part("name") name: String,
        @Part image: MultipartBody.Part
    ): ApiResponse
}
data class ApiResponse(
    val status: String,
    val photo_url: String
)