package com.example.zerowaste.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.zerowaste.data.model.Grocery

@Database(entities = [Grocery::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groceryDao(): GroceryDao
}
