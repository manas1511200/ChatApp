package com.example.chatapp.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import uriToMultipart
import java.util.jar.Manifest

class LoginViewModel : ViewModel() {
    //The database thing
    private val authService = RetrofitClient.create()

    // User Input States
    var username by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var profileImageUri by mutableStateOf<Uri?>(null)
        private set
    var isSignUp by mutableStateOf(true)
        private set
    var rePassword by mutableStateOf("")
        private set
    var showBottomSheet by mutableStateOf(false)
    var showEmojiPicker by mutableStateOf(false)
    val selectedEmoji by mutableStateOf("😀") // Default emoji
    val emojiList = listOf(
        // Faces & Emotions
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
        "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
        "😋", "😜", "😝", "😛", "🤑", "🤗", "🤩", "🤔", "🤨", "😐",
        "😑", "😶", "🙄", "😏", "😒", "😞", "😔", "😟", "😕", "🙁",
        "☹️", "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠",
        "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥",
        "😓", "🤤", "😪", "😴", "😷", "🤒", "🤕", "🤢", "🤮", "🤧",
        // Gestures & Hand Signs
        "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤏", "✌️", "🤞", "🤟",
        "🤘", "🤙", "👈", "👉", "👆", "👇", "☝️", "👍", "👎", "✊",
        "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🙏"
    )


    // Activity Result Launchers (initialized externally, e.g., in the composable)
    var takePictureLauncher: ActivityResultLauncher<Uri>? = null
    var pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null

    //eror messages


    //handlers
    fun handleCameraResult(uri: Uri?) {
        uri?.let {
            profileImageUri = uri
        }
    }

    fun handleGalleryResult(uri: Uri?) {
        uri?.let {
            profileImageUri = uri
        }
    }

    fun onGallerySelected() {
        pickImageLauncher?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    var onEmojiSelected: () -> Unit = {}

    // UI States
    var isLoading by mutableStateOf(false)
        private set
    var showAlert by mutableStateOf(false)
    var alertMessage by mutableStateOf("")
        private set

    // --- Update Functions ---
    fun updateUsername(newUsername: String) {
        username = newUsername
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun updateRePassword(newRePassword: String) {
        rePassword = newRePassword
    }

    fun updateProfileImageUri(uri: Uri?) {
        profileImageUri = uri
    }

    // --- Auth Mode ---
    fun toggleAuthMode() {
        isSignUp = !isSignUp
        clearForm()
    }

    // --- Form Management ---
    private fun clearForm() {
        username = ""
        email = ""
        password = ""
        rePassword = ""
        profileImageUri = null
        showAlert = false
        alertMessage = ""
    }

    // --- Create Profile ---
    fun createProfile(onSuccess: () -> Unit, context: Context) {
        if (isSignUp && password != rePassword) { // Compare passwords only during signup
            showAlert("Passwords don't match!")
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                validateInputs()
                val multipartProfilePhoto =
                    uriToMultipart(context = context, uri = profileImageUri!!)
                val respose = authService.registerUser(
                    username.toRequestBody(),
                    email.toRequestBody(),
                    password.toRequestBody(),
                    multipartProfilePhoto
                )
                if (respose.isSuccessful) {
                    onSuccess()
                } else {
                     val error = respose.errorBody()?.string()
                    showAlert(error.toString())
                }// Access errorBody from respose
            } catch (e: Exception) {
                showAlert(e.message ?: "An error occurred")
            } finally {
                isLoading = false
            }
        }
    }

    // --- Input Validation ---
    private fun validateInputs() {
        when {
            email.isBlank() -> throw IllegalArgumentException("Email is required")
            password.isBlank() -> throw IllegalArgumentException("Password is required")
            isSignUp && username.isBlank() -> throw IllegalArgumentException("Username is required")
        }
    }
    // --- Alert Management ---
    fun showAlert(message: String) {
        alertMessage = message
        showAlert = true
    }

    fun dismissAlert() {
        showAlert = false
        alertMessage = ""
    }

    // --- Dummy Functions (for UI interaction) ---
    fun onEmojiSelected() {
        showAlert("emoji selected")
    }


    fun updateShowBottomSheet(show: Boolean) {
        showBottomSheet = show
    }
}

