package com.example.zerowaste.ui.recipe

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    val recipes by viewModel.recipes.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Recipes", style = MaterialTheme.typography.headlineLarge)
            if (recipes.isNotEmpty() && isGenerating) {
                Spacer(modifier = Modifier.padding(start = 16.dp))
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // The main content area
        Box(modifier = Modifier.fillMaxSize()) {
            if (recipes.isEmpty()) {
                if (isGenerating) {
                    // Initial loading state
                    val transition = rememberInfiniteTransition(label = "loading_dots")
                    val dotCount by transition.animateValue(
                        initialValue = 1,
                        targetValue = 4,
                        typeConverter = Int.VectorConverter,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1500),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "dot_count"
                    )
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Agent is generating recipes" + ".".repeat(dotCount),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    // Empty state after loading is finished
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No ingredients to make recipes with!")
                    }
                }
            } else {
                // Display the list of recipes
                LazyColumn {
                    items(recipes) { recipe ->
                        RecipeItem(recipe = recipe)
                    }
                }
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
