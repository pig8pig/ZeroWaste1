package com.example.zerowaste.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.zerowaste.data.model.Grocery
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {
    @Query("SELECT * FROM groceries ORDER BY daysToExpiry ASC LIMIT 3")
    fun getExpiringSoon(): Flow<List<Grocery>>

    @Query("SELECT * FROM groceries")
    fun getAllGroceries(): Flow<List<Grocery>>

    @Query("SELECT * FROM groceries WHERE daysToExpiry = 0")
    fun getExpiringToday(): Flow<List<Grocery>>

    @Query("SELECT * FROM groceries WHERE daysToExpiry = 1")
    fun getExpiringTomorrow(): Flow<List<Grocery>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groceries: List<Grocery>)

    @Query("SELECT COUNT(*) FROM groceries")
    suspend fun count(): Int
}
