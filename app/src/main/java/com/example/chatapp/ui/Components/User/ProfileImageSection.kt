// ProfileImageSection.kt
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.chatapp.R
import com.example.chatapp.viewmodel.LoginViewModel
import createImageUri
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageSection(
    viewModel: LoginViewModel
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.takePictureLauncher?.let { tempImageUri?.let(it::launch) }
                ?: viewModel.showAlert("Failed to create image file")
        } else {
            viewModel.showAlert("Camera permission required")
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            viewModel.updateProfileImageUri(tempImageUri)
        } else {
            viewModel.showAlert("Failed to capture image")
        }
        tempImageUri = null
        viewModel.updateShowBottomSheet(false)
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(viewModel::updateProfileImageUri)
        viewModel.updateShowBottomSheet(false)
    }

    LaunchedEffect(Unit) {
        viewModel.takePictureLauncher = takePictureLauncher
        viewModel.pickImageLauncher = pickImageLauncher
    }

    Card(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable(enabled = viewModel.isSignUp) { viewModel.updateShowBottomSheet(true) },
        elevation = CardDefaults.cardElevation(12.dp),
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
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
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (viewModel.isSignUp) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { viewModel.updateShowBottomSheet(true) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    if (viewModel.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.updateShowBottomSheet(false) },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Profile Photo",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

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
                                    tempImageUri = createImageUri(context)
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        tempImageUri?.let(takePictureLauncher::launch)
                                    } else {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp)
                    ) {
                        Column {
                            Icon(
                                painter = painterResource(R.drawable.ic_camera),
                                contentDescription = "Camera",
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Camera",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) viewModel.onGallerySelected()
                            }
                        },
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp)
                    ) {
                        Column {
                            Icon(
                                painter = painterResource(R.drawable.ic_gallery),
                                contentDescription = "Gallery",
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Gallery",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}