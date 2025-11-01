package com.example.zerowaste.ui.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ScanScreen(
    type: String, // "in" or "out"
    onConfirm: () -> Unit, // Simplified callback
    viewModel: ScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            bitmap?.let {
                viewModel.identifyFood(it, type)
            }
        }
    )

    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission) {
            if (uiState is ScanUiState.Initial) {
                cameraLauncher.launch(null)
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (hasCameraPermission) {
            when (val state = uiState) {
                is ScanUiState.Initial -> {
                    Text(text = "Let's scan your food item", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { cameraLauncher.launch(null) }) {
                        Text("Open Camera")
                    }
                }
                is ScanUiState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Identifying food...", style = MaterialTheme.typography.bodyLarge)
                }
                is ScanUiState.Success -> {
                    Text(text = "Identified Food:", style = MaterialTheme.typography.titleLarge)
                    Text(text = state.foodName, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    // TODO: Add quantity and expiry date input fields here
                    Button(onClick = {
                        // For now, saving with dummy data
                        viewModel.saveGrocery(state.foodName, 1, 7, "Unknown")
                        onConfirm()
                    }) {
                        Text("Confirm")
                    }
                }
                is ScanUiState.Error -> {
                    Text(text = "Error", style = MaterialTheme.typography.titleLarge)
                    Text(text = state.message, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { cameraLauncher.launch(null) }) {
                        Text("Try Again")
                    }
                }
            }
        } else {
            Text("Camera permission is required to scan food items.")
        }
    }
}
