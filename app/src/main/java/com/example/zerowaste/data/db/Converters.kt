package com.example.zerowaste.data.db

import androidx.room.TypeConverter
import com.example.zerowaste.data.model.IngredientNeeded
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromIngredientNeededList(value: List<IngredientNeeded>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toIngredientNeededList(value: String): List<IngredientNeeded> {
        return Json.decodeFromString(value)
    }
}
