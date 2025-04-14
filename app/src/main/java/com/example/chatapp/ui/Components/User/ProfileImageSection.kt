package com.example.chatapp.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.chatapp.R
import com.example.chatapp.utils.createImageUri // Make sure this function is correct!
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

    // State to hold the temporary image URI during capture
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    //Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && tempImageUri != null) {
            viewModel.updateProfileImageUri(tempImageUri) // Update ViewModel with the temp Uri
            viewModel.takePictureLauncher?.launch(tempImageUri!!) //Non-null assertion safe as we're inside the check
        } else {
            viewModel.showAlert(if (isGranted) "Could not create image file" else "Camera permission required")
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempImageUri != null) {
            //The image is already written to tempImageUri, no need to copy
            viewModel.updateProfileImageUri(tempImageUri)
        }
        tempImageUri = null // clear the temp Uri after capture (success or failure)
        viewModel.updateShowBottomSheet(false)
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
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
            when {
                viewModel.profileImageUri != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(viewModel.profileImageUri),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(R.drawable.default0),
                        contentDescription = "Profile",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

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
                    IconButton(
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    // Create temp uri and launch permission check
                                    tempImageUri = createImageUri(context)
                                    val permission = Manifest.permission.CAMERA
                                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                        //if already granted, just launc take picture
                                        viewModel.handleCameraResult(tempImageUri)
                                        tempImageUri?.let { input -> takePictureLauncher.launch(input) } // Non-null asserted in function
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
                                    modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
        }
    }
}