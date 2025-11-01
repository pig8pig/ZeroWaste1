package com.example.zerowaste.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val recipe_name: String,
    val description: String,
    val prep_time: String,
    val cook_time: String,
    val difficulty: String,
    val servings: Int,
    val ingredients_used: List<String>,
    val ingredients_needed: List<IngredientNeeded>,
    val instructions: List<String>,
    val waste_prevention_score: Int,
    val notes: String
)

@Serializable
data class IngredientNeeded(
    val name: String,
    val quantity: String,
    val optional: Boolean
)
