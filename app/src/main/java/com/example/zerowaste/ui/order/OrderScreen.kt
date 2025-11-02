package com.example.zerowaste.ui.order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OrderScreen(viewModel: OrderViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // local UI-managed ordering state so we can animate per-item loading/checks
    val coroutineScope = rememberCoroutineScope()
    val localChecked = remember { mutableStateListOf<Boolean>() }
    var isOrdering by remember { mutableStateOf(false) }
    var showLocalSuccessDialog by remember { mutableStateOf(false) }

    // when the groceries list changes, initialize the local checked list to match (or reset)
    LaunchedEffect(key1 = uiState) {
        if (uiState is OrderUiState.Success) {
            val groceries = (uiState as OrderUiState.Success).groceries
            localChecked.clear()
            groceries.forEach { localChecked.add(it.isChecked) }
            // ensure localChecked length equals groceries length
            if (localChecked.size < groceries.size) {
                repeat(groceries.size - localChecked.size) { localChecked.add(false) }
            }
        } else {
            localChecked.clear()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Shopping List", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(24.dp))

        when (val state = uiState) {
            is OrderUiState.Initial -> {
                // This state is very brief, so often it's fine to show nothing.
            }

            is OrderUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Loading expiring groceries...", style = MaterialTheme.typography.bodyLarge)
                }
            }

            is OrderUiState.Success -> {
                if (state.groceries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No expiring groceries found.", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Suggested based on your pantry:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f) // Ensures the list takes up available space
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            items(state.groceries, key = { it.grocery.id }) { item ->
                                val index = state.groceries.indexOf(item)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.grocery.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${item.grocery.quantity} ${item.grocery.units}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.weight(0.1f))

                                    // decide whether to display loader or check: priority:
                                    // - if ordering: use localChecked to animate sequentially
                                    // - otherwise show the item's actual isChecked from state
                                    val checked = localChecked.getOrNull(index) ?: item.isChecked

                                    Box(modifier = Modifier.size(24.dp)) {
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = !checked,
                                            enter = fadeIn(),
                                            exit = fadeOut(animationSpec = tween(durationMillis = 500))
                                        ) {
                                            if (isOrdering) {
                                                CircularProgressIndicator(
                                                    strokeWidth = 2.dp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            } else {
                                                Box(modifier = Modifier.size(20.dp))
                                            }
                                        }

                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = checked,
                                            enter = fadeIn(animationSpec = tween(delayMillis = 200)),
                                            exit = fadeOut()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Checked",
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .background(Color.Transparent, CircleShape)
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                // start UI-side simulation while also calling viewModel.checkout() for any side-effects
                                if (!isOrdering) {
                                    viewModel.checkout()
                                    isOrdering = true
                                    // launch sequential simulation over localChecked
                                    coroutineScope.launch {
                                        // ensure list length matches groceries
                                        val count = state.groceries.size
                                        // If localChecked is shorter/longer adjust (should be in sync via LaunchedEffect but guard here)
                                        while (localChecked.size < count) localChecked.add(false)
                                        if (localChecked.size > count) {
                                            repeat(localChecked.size - count) { localChecked.removeAt(localChecked.lastIndex) }
                                        }

                                        for (i in 0 until count) {
                                            // show loader for a little bit before marking checked
                                            // keep each item "loading" visible for ~600ms
                                            delay(600)
                                            localChecked[i] = true
                                        }
                                        // little pause after all checks
                                        delay(300)
                                        isOrdering = false
                                        showLocalSuccessDialog = true
                                        // optionally inform ViewModel the dialog should appear (if it manages it)
                                        // we call dismissSuccessDialog only when user dismisses below to keep things consistent
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOrdering) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Color(0xFF4CAF50)
                            ),
                            enabled = !isOrdering
                        ) {
                            Text(if (isOrdering) "Ordering..." else "Checkout", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // If the ViewModel reported success dialog flag we still respect it (keeps compatibility).
                if (state.showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissSuccessDialog() },
                        title = { Text(text = "Order Successful") },
                        text = { Text("Your groceries have been ordered.") },
                        confirmButton = {
                            TextButton(onClick = { viewModel.dismissSuccessDialog() }) {
                                Text("OK")
                            }
                        }
                    )
                }

                // Local dialog shown after UI simulation completes
                if (showLocalSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showLocalSuccessDialog = false
                            viewModel.dismissSuccessDialog()
                        },
                        title = { Text(text = "Order Successful") },
                        text = { Text("Your groceries have been ordered.") },
                        confirmButton = {
                            TextButton(onClick = {
                                showLocalSuccessDialog = false
                                viewModel.dismissSuccessDialog()
                            }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }

            is OrderUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchExpiringGroceries() }) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}