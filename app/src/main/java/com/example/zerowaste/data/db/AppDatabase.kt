package com.example.zerowaste.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.zerowaste.data.model.Grocery
import com.example.zerowaste.data.model.Recipe

@Database(entities = [Grocery::class, Recipe::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groceryDao(): GroceryDao
    abstract fun recipeDao(): RecipeDao
}
