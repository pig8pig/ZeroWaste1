package com.example.zerowaste.ui.recipe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zerowaste.data.db.GroceryDao
import com.example.zerowaste.data.db.RecipeDao
import com.example.zerowaste.data.model.Grocery
import com.example.zerowaste.data.model.Recipe
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val groceryDao: GroceryDao,
    private val recipeDao: RecipeDao
) : ViewModel() {

    val recipes: StateFlow<List<Recipe>> = recipeDao.getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    init {
        viewModelScope.launch {
            groceryDao.getAllGroceries()
                .debounce(2000L)
                .distinctUntilChanged() // Prevent re-triggering if the list hasn't changed
                .collect { groceries ->
                    generateAndCacheRecipes(groceries)
                }
        }
    }

    private fun generateAndCacheRecipes(groceries: List<Grocery>) {
        viewModelScope.launch {
            if (groceries.isEmpty()) {
                recipeDao.clearRecipes()
                return@launch
            }
            
            _isGenerating.value = true
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = "AIzaSyBEV7Vy_PJS7nLgF_Sizw2d9RDAagdeU8E"
                )

                val prompt = buildPrompt(groceries)
                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: ""

                val startIndex = responseText.indexOfFirst { it == '[' }
                val endIndex = responseText.indexOfLast { it == ']' }
                val cleanedResponse = if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    responseText.substring(startIndex, endIndex + 1)
                } else {
                    return@launch
                }

                val json = Json { ignoreUnknownKeys = true }
                val recipeList = json.decodeFromString<List<Recipe>>(cleanedResponse)
                
                recipeDao.clearAndInsert(recipeList)

            } catch (e: Exception) {
                Log.e("RecipeViewModel", "Error generating recipes", e)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private fun buildPrompt(groceries: List<Grocery>): String {
        val ingredientsList = groceries.joinToString("\n") { 
            "- ${it.name} (${it.quantity} ${it.units}) - expires in ${it.daysToExpiry} days"
        }

        return """You are an expert chef specializing in minimizing food waste. Generate recipe suggestions based on available ingredients.

Available Ingredients:
$ingredientsList

User Preferences:
- Dietary restrictions: None
- Cuisine preference: Any
- Max cooking time: 60 minutes
- Skill level: Any

Instructions:
1. Prioritize ingredients that expire soonest.
2. Generate 3 different recipe options.
3. For each recipe, indicate which ingredients from the list you're using.
4. Suggest any missing essential ingredients (keep it minimal).

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
    }
}
