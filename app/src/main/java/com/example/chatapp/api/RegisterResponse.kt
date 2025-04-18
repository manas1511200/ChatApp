import com.google.gson.annotations.SerializedName

// File: RegisterResponse.kt
data class RegisterResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: User
)

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("profilePhoto") val profilePhoto: String
)
data class UpdateResponse(
    @SerializedName("message") val message: String
)