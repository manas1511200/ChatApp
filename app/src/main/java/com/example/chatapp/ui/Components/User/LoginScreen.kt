package com.example.chatapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (viewModel.isSignUp) "Create Account" else "Welcome Back",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )


            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LoginContent(
                viewModel = viewModel,
                onProfileCreated = { navController.navigate("chat") }
            )

            if (viewModel.showAlert) {
                AlertDialog(
                    onDismissRequest = { viewModel.showAlert = false },
                    title = { Text("Notice") },
                    text = { Text(viewModel.alertMessage) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.showAlert = false
                                if (viewModel.showBottomSheet) {
                                    viewModel.showBottomSheet = false
                                }
                            }
                        ) {
                            Text("OK")
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
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImageSection(viewModel = viewModel)
        Spacer(modifier = Modifier.height(32.dp))

        if (viewModel.isSignUp) {
            OutlinedTextField(
                value = viewModel.username,
                onValueChange = { viewModel.updateUsername(it) },
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
            onValueChange = { viewModel.updateEmail(it) },
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
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
    if(viewModel.isSignUp) {
        OutlinedTextField(
            value = viewModel.rePassword,
            onValueChange = { viewModel.updateRePassword(it) },
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
            onClick = { viewModel.createProfile(onProfileCreated) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !viewModel.isLoading,
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(4.dp)
        ) {
            Text(
                text = if (viewModel.isLoading) "Processing..."
                else if (viewModel.isSignUp) "Create Account"
                else "Sign In",
                fontSize = MaterialTheme.typography.labelLarge.fontSize
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { viewModel.toggleAuthMode() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (viewModel.isSignUp) "Already have an account? Sign In"
                else "Don't have an account? Create One",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}