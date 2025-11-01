package com.example.zerowaste.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groceries")
data class Grocery(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val units: String,
    val daysToExpiry: Int,
    val type: String
)
