package com.example.zerowaste.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.zerowaste.data.db.Converters
import kotlinx.serialization.Serializable

@Entity(tableName = "recipes")
@Serializable
@TypeConverters(Converters::class)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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
