package com.example.chatapp.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.chatapp.R
import com.example.chatapp.utils.createImageUri // Ensure this uses FileProvider
import com.example.chatapp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageSection(
    viewModel: LoginViewModel
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // Camera permission launcher [[7]][[9]]
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tempImageUri?.let { uri ->
                viewModel.takePictureLauncher?.launch(uri)
            } ?: viewModel.showAlert("Failed to create image file")
        } else {
            viewModel.showAlert("Camera permission required")
        }
    }

    // Take picture launcher [[3]][[7]]
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            viewModel.updateProfileImageUri(tempImageUri)
        }
        tempImageUri = null
        viewModel.updateShowBottomSheet(false)
    }

    // Gallery picker launcher [[3]]
    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.updateProfileImageUri(it) }
        viewModel.updateShowBottomSheet(false)
    }

    // Initialize ViewModel launchers
    LaunchedEffect(Unit) {
        viewModel.takePictureLauncher = takePictureLauncher
        viewModel.pickImageLauncher = pickImageLauncher
    }

    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable(enabled = viewModel.isSignUp) { viewModel.updateShowBottomSheet(true) },
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Display image or default icon
            if (viewModel.profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(viewModel.profileImageUri),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.default0),
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Edit button for signup mode
            if (viewModel.isSignUp) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    // Bottom sheet for image selection
    if (viewModel.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.updateShowBottomSheet(false) },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .animateContentSize()
            ) {
                Text(
                    text = "Select Profile Photo",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Camera option
                    IconButton(
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    tempImageUri = createImageUri(context) // Must use FileProvider [[8]]
                                    val permission = Manifest.permission.CAMERA
                                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                        tempImageUri?.let { takePictureLauncher.launch(it) }
                                    } else {
                                        cameraPermissionLauncher.launch(permission)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_camera),
                            contentDescription = "Camera",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Gallery option
                    IconButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    viewModel.onGallerySelected()
                                }
                            }
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_gallery),
                            contentDescription = "Gallery",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Emoji option
                    IconButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                viewModel.onEmojiSelected()
                            }
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.default6),
                            contentDescription = "Emoji",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                if(viewModel.showEmojiPicker) {
                    EmojiPicker(
                        emojiList = viewModel.emojiList,
                        onEmojiSelected = { emoji ->
                            viewModel.selectedEmoji = emoji
                            viewModel.showEmojiPicker = false
                            viewModel.updateShowBottomSheet(false)
                        },
                        onDismiss = {
                            viewModel.showEmojiPicker = false
                        }
                    )
                }
            }
        }
    }
}