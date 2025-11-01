package com.example.zerowaste.ui.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.db.GroceryDao
import com.example.zerowaste.data.model.Grocery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val groceryDao: GroceryDao
) : ViewModel() {

    private val _scannedGrocery = MutableStateFlow<Grocery?>(null)
    val scannedGrocery: StateFlow<Grocery?> = _scannedGrocery

    fun scanFood(type: String) {
        viewModelScope.launch {
            // Mocked data for a scanned food item
            val grocery = if (type == "in") {
                Grocery(name = "Apples", quantity = 1, units = "pcs", daysToExpiry = 5)
            } else {
                groceryDao.getFirstExpiringGroceryByName("Apples")
            }
            _scannedGrocery.value = grocery
        }
    }

    fun confirmScan(type: String, grocery: Grocery) {
        viewModelScope.launch {
            if (type == "in") {
                val existingGrocery = groceryDao.getGroceryByNameAndExpiry(grocery.name, grocery.daysToExpiry)
                if (existingGrocery != null) {
                    groceryDao.update(existingGrocery.copy(quantity = existingGrocery.quantity + 1))
                } else {
                    groceryDao.insert(grocery)
                }
            } else {
                if (grocery.quantity > 1) {
                    groceryDao.update(grocery.copy(quantity = grocery.quantity - 1))
                } else {
                    groceryDao.delete(grocery)
                }
            }
            _scannedGrocery.value = null
        }
    }

    fun rescan() {
        _scannedGrocery.value = null
    }
}

@Composable
fun ScanScreen(
    type: String,
    viewModel: ScanViewModel = hiltViewModel(),
    onConfirm: () -> Unit
) {
    val scannedGrocery by viewModel.scannedGrocery.collectAsState()

    LaunchedEffect(type) {
        if (scannedGrocery == null) {
            viewModel.scanFood(type)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (scannedGrocery != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Scanned: ${scannedGrocery!!.name}")
                Text(text = "Quantity: ${scannedGrocery!!.quantity}")
                Text(text = "Units: ${scannedGrocery!!.units}")
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        viewModel.confirmScan(type, scannedGrocery!!)
                        onConfirm()
                    }) {
                        Text("Confirm")
                    }
                    Button(onClick = { viewModel.rescan() }) {
                        Text("Rescan")
                    }
                }
            }
        } else {
            Text(text = "Scanning food $type...")
        }
    }
}
