package com.example.zerowaste.ui.scan

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.db.GroceryDao
import com.example.zerowaste.data.model.Grocery
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScanUiState {
    object Initial : ScanUiState
    object Loading : ScanUiState
    data class Success(val foodName: String) : ScanUiState
    data class Error(val message: String) : ScanUiState
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val groceryDao: GroceryDao
) : ViewModel() {

    private val _uiState: MutableStateFlow<ScanUiState> = MutableStateFlow(ScanUiState.Initial)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun identifyFood(image: Bitmap, type: String) {
        viewModelScope.launch {
            _uiState.value = ScanUiState.Loading

            val generativeModel = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = "AIzaSyBEV7Vy_PJS7nLgF_Sizw2d9RDAagdeU8E"
            )

            val prompt = "Identify the single most prominent food item in this image. Provide only a simple, one-to-three word name for the food (e.g., 'Banana', 'Red Apple', 'Chicken Breast'). Do not add any other descriptive text or markdown."

            try {
                val inputContent = content {
                    image(image)
                    text(prompt)
                }

                val response = generativeModel.generateContent(inputContent)
                val foodName = response.text?.trim() ?: "Could not identify food"
                _uiState.value = ScanUiState.Success(foodName)

            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(e.localizedMessage ?: "An unknown error occurred")
            }
        }
    }

    fun saveGrocery(foodName: String, quantity: Int, daysToExpiry: Int, type: String) {
        viewModelScope.launch {
            val existingGrocery = groceryDao.getGroceryByNameAndExpiry(foodName, daysToExpiry)
            if (existingGrocery != null) {
                val updatedGrocery = existingGrocery.copy(quantity = existingGrocery.quantity + quantity)
                groceryDao.update(updatedGrocery)
            } else {
                val newGrocery = Grocery(name = foodName, quantity = quantity, daysToExpiry = daysToExpiry, type = type, units = "pcs") // Using placeholder for units
                groceryDao.insert(newGrocery)
            }
        }
    }
}
