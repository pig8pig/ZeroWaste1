package com.example.zerowaste.ui.recipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zerowaste.data.model.Recipe

@Composable
fun RecipeScreen(viewModel: RecipeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { viewModel.generateRecipes() }) {
            Text("Generate Recipes")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is RecipeUiState.Initial -> {
                Text("Click the button to generate recipes.")
            }
            is RecipeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RecipeUiState.Success -> {
                LazyColumn {
                    items(state.recipes) { recipe ->
                        RecipeItem(recipe = recipe)
                    }
                }
            }
            is RecipeUiState.Error -> {
                Text("Error: ${state.message}")
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = recipe.recipe_name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = recipe.description)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Ingredients Used:", style = MaterialTheme.typography.titleMedium)
            recipe.ingredients_used.forEach { ingredient ->
                Text(text = "- $ingredient")
            }
        }
    }
}
