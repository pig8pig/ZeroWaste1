package com.example.zerowaste.ui.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.model.Recipe
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

sealed interface RecipeUiState {
    object Initial : RecipeUiState
    object Loading : RecipeUiState
    data class Success(val recipes: List<Recipe>) : RecipeUiState
    data class Error(val message: String) : RecipeUiState
}

@HiltViewModel
class RecipeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Initial)
    val uiState: StateFlow<RecipeUiState> = _uiState

    private val API_KEY = "AIzaSyBOodH4-cixzkYcoYhlge2tgFFWcV8yNFg"

    fun generateRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = API_KEY
            )

            val prompt = """You are an expert chef specializing in minimizing food waste. Generate recipe suggestions based on available ingredients.

Available Ingredients:
- Chicken breast (500g) - expires in 2 days
- Spinach (200g) - expires in 1 days
- Tomatoes (4 medium) - expires in 3 days
- Pasta (300g) - expires in 180 days
- Garlic (1 bulb) - expires in 14 days

User Preferences:
- Dietary restrictions: None
- Cuisine preference: Italian or Mediterranean
- Max cooking time: 45 minutes
- Skill level: Intermediate

Instructions:
1. Prioritize ingredients that expire soonest
2. Generate 3 different recipe options
3. For each recipe, indicate which ingredients from the list you're using
4. Suggest any missing essential ingredients (keep it minimal)
5. Include substitutions for missing items when possible

Return your response as a JSON array with this structure:
[
  {
    "recipe_name": "Recipe Name",
    "description": "Brief description",
    "prep_time": "15 minutes",
    "cook_time": "30 minutes",
    "difficulty": "Easy/Medium/Hard",
    "servings": 4,
    "ingredients_used": ["ingredient1", "ingredient2"],
    "ingredients_needed": [
      {"name": "item", "quantity": "amount", "optional": false}
    ],
    "instructions": [
      "Step 1",
      "Step 2"
    ],
    "waste_prevention_score": 8,
    "notes": "Any tips or substitutions"
  }
]

Ensure the JSON is valid and properly formatted."""

            try {
                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: ""
                val cleanedResponse = responseText.removePrefix("```json").removeSuffix("```").trim()
                val json = Json { ignoreUnknownKeys = true }
                val recipeList = json.decodeFromString<List<Recipe>>(cleanedResponse)
                _uiState.value = RecipeUiState.Success(recipeList)
            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error generating or parsing recipes", e)
                _uiState.value = RecipeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
