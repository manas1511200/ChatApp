// File: AuthService.kt
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @Multipart
    @POST("api/auth/register")
    suspend fun registerUser(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part profilePhoto: MultipartBody.Part
    ): Response<RegisterResponse>

    @Multipart
    @PUT("api/user/{username}")
    suspend fun updateProfile(
        @Path("username") username: String,
        @Part profilePhoto: MultipartBody.Part
    ): Response<UpdateResponse>
}