package com.example.chatapp.viewmodel

import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.jar.Manifest

class LoginViewModel : ViewModel() {
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

    // Activity Result Launchers (initialized externally, e.g., in the composable)
    var takePictureLauncher: ActivityResultLauncher<Uri>? = null
    var pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null
    //handlers
    fun handleCameraResult(uri: Uri?){
        uri?.let {
            profileImageUri = uri
        }
    }
    fun handleGalleryResult(uri: Uri?){
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
    fun createProfile(onSuccess: () -> Unit) {
        if (isSignUp && password != rePassword) { // Compare passwords only during signup
            showAlert("Passwords don't match!")
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                validateInputs()
                delay(1500) // Simulate network call
                onSuccess()
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
    fun onEmojiSelected() = showAlert("Emoji selected")


    fun updateShowBottomSheet(show: Boolean) {
        showBottomSheet = show
    }
}