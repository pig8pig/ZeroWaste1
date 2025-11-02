package com.example.zerowaste.ui.recipe

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
        // Top bar: use fillMaxWidth() not fillMaxSize()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Recipes",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.weight(1f)
            )

            // Top-right: spinner while generating, otherwise refresh button
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(28.dp),
                    strokeWidth = 3.dp
                )
            } else {
                IconButton(onClick = { viewModel.refreshRecipes() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh recipes"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Small debug/status line so you can see what's happening with the flows:
        Text(
            text = "Debug: recipes=${recipes.size}, isGenerating=$isGenerating",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Main content area — give the box the remaining space
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            if (recipes.isEmpty()) {
                if (isGenerating) {
                    // animated "loading dots" message while generating first batch
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
                    // Empty state when not generating
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No ingredients to make recipes with!")
                    }
                }
            } else {
                // Display the list of recipes — LazyColumn fills the available space
                LazyColumn(modifier = Modifier.fillMaxSize()) {
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