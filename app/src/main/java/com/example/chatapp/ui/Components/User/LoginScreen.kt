// LoginScreen.kt
package com.example.chatapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.ui.components.ProfileImageSection
import com.example.chatapp.viewmodel.LoginViewModel

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController = rememberNavController(),
    viewModel: LoginViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 4.dp
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (viewModel.isSignUp) "Create Account" else "Welcome Back",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LoginContent(viewModel = viewModel, onProfileCreated = { navController.navigate("chat") })

            if (viewModel.showAlert) {
                AlertDialog(
                    onDismissRequest = { viewModel.showAlert = false },
                    title = { Text("Notice", style = MaterialTheme.typography.titleMedium) },
                    text = { Text(viewModel.alertMessage, style = MaterialTheme.typography.bodyMedium) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.showAlert = false
                                if (viewModel.showBottomSheet) viewModel.updateShowBottomSheet(false)
                            }
                        ) {
                            Text("OK", style = MaterialTheme.typography.labelLarge)
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun LoginContent(
    viewModel: LoginViewModel,
    onProfileCreated: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        ProfileImageSection(viewModel = viewModel)
        Spacer(modifier = Modifier.height(32.dp))

        if (viewModel.isSignUp) {
            OutlinedTextField(
                value = viewModel.username,
                onValueChange = viewModel::updateUsername,
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = viewModel::updateEmail,
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = viewModel::updatePassword,
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        if (viewModel.isSignUp) {
            OutlinedTextField(
                value = viewModel.rePassword,
                onValueChange = viewModel::updateRePassword,
                label = { Text("Confirm Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { if (!viewModel.isLoading) viewModel.createProfile(onProfileCreated,context) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !viewModel.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.isLoading)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = if (viewModel.isSignUp) "Create Account" else "Sign In",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = viewModel::toggleAuthMode,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (viewModel.isSignUp) "Already have an account? Sign In" else "Don't have an account? Create One",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
    }
}