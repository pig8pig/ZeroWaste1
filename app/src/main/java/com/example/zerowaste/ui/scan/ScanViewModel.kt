package com.example.zerowaste.ui.scan

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.db.GroceryDao
import com.example.zerowaste.data.model.Grocery
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.zerowaste.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
data class ScannedFoodItem(
    val name: String,
    val quantity: Int,
    val units: String,
    val daysToExpiry: Int,
    val type: String
)

sealed interface ScanUiState {
    object Initial : ScanUiState
    object Loading : ScanUiState
    data class Success(val scannedFood: ScannedFoodItem) : ScanUiState
    data class Error(val message: String) : ScanUiState
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val groceryDao: GroceryDao
) : ViewModel() {

    private val _uiState: MutableStateFlow<ScanUiState> = MutableStateFlow(ScanUiState.Initial)
    private val apiKey = BuildConfig.GEMINI_API_KEY
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun identifyFood(image: Bitmap) {
        viewModelScope.launch {
            _uiState.value = ScanUiState.Loading

            val generativeModel = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = apiKey
            )

            val prompt = """Analyze the image of the food item. Your goal is to identify the food and estimate its properties. Respond ONLY with a valid JSON object.

Your JSON response must contain these exact fields:
- "name": The common name of the food item (e.g., "Banana", "Red Apple").
- "quantity": An integer estimate of the number of items if countable, otherwise 1.
- "units": The unit of measurement (e.g., "pcs", "head", "bunch", "L", "g").
- "daysToExpiry": An integer estimate of the average shelf life in days from today for this type of fresh item.
- "type": The general food category (e.g., "Fruit", "Vegetable", "Dairy", "Meat", "Bakery", "Pantry", "Leftovers").

Example for an image of a single banana:
{
  "name": "Banana",
  "quantity": 1,
  "units": "pcs",
  "daysToExpiry": 5,
  "type": "Fruit"
}

Do not include any other text, explanations, or markdown like ```json in your response. Provide only the raw JSON object."""

            try {
                val inputContent = content {
                    image(image)
                    text(prompt)
                }

                val response = generativeModel.generateContent(inputContent)
                val responseText = response.text ?: "{}"
                Log.d("ScanViewModel", "Raw AI Response: $responseText")
                val json = Json { ignoreUnknownKeys = true }
                val scannedItem = json.decodeFromString<ScannedFoodItem>(responseText)

                _uiState.value = ScanUiState.Success(scannedItem)

            } catch (e: Exception) {
                Log.e("ScanViewModel", "Error parsing food item", e)
                _uiState.value = ScanUiState.Error(e.localizedMessage ?: "Could not parse the response from the AI.")
            }
        }
    }

    fun saveGrocery(scannedFoodItem: ScannedFoodItem) {
        viewModelScope.launch {
            val existingGrocery = groceryDao.getGroceryByNameAndExpiry(scannedFoodItem.name, scannedFoodItem.daysToExpiry)

            if (existingGrocery != null) {
                val updatedGrocery = existingGrocery.copy(quantity = existingGrocery.quantity + scannedFoodItem.quantity)
                groceryDao.update(updatedGrocery)
            } else {
                val newGrocery = Grocery(
                    name = scannedFoodItem.name,
                    quantity = scannedFoodItem.quantity,
                    units = scannedFoodItem.units,
                    daysToExpiry = scannedFoodItem.daysToExpiry,
                    type = scannedFoodItem.type
                )
                groceryDao.insert(newGrocery)
            }
        }
    }

    fun removeGrocery(scannedFoodItem: ScannedFoodItem) {
        viewModelScope.launch {
            var quantityToRemove = scannedFoodItem.quantity

            while (quantityToRemove > 0) {
                val groceryToRemove = groceryDao.getFirstExpiringGroceryByName(scannedFoodItem.name)

                if (groceryToRemove == null) {
                    // No more items of this name in the database, so stop.
                    break
                }

                if (groceryToRemove.quantity > quantityToRemove) {
                    // This item has more quantity than we need to remove.
                    // Reduce its quantity and we are done.
                    val newQuantity = groceryToRemove.quantity - quantityToRemove
                    groceryDao.update(groceryToRemove.copy(quantity = newQuantity))
                    quantityToRemove = 0 // Exit loop
                } else {
                    // This item's quantity is less than or equal to what we need to remove.
                    // Remove this item completely and continue the loop.
                    quantityToRemove -= groceryToRemove.quantity
                    groceryDao.delete(groceryToRemove)
                }
            }
        }
    }
}
