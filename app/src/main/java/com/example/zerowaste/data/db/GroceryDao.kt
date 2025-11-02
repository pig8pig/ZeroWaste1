package com.example.zerowaste.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.zerowaste.data.model.Grocery
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {
    @Query("SELECT * FROM groceries ORDER BY daysToExpiry ASC LIMIT 5")
    fun getExpiringSoon(): Flow<List<Grocery>>

    @Query("SELECT * FROM groceries ORDER BY daysToExpiry ASC")
    fun getAllGroceries(): Flow<List<Grocery>>

    @Query("SELECT * FROM groceries")
    suspend fun getAll(): List<Grocery>

    @Query("SELECT * FROM groceries WHERE daysToExpiry < 0")
    suspend fun getExpired(): List<Grocery>

    @Query("SELECT * FROM groceries WHERE daysToExpiry >= 0 AND daysToExpiry <= 1")
    suspend fun getExpiringSoonList(): List<Grocery>

    @Query("SELECT * FROM groceries WHERE daysToExpiry = 0")
    fun getExpiringToday(): Flow<List<Grocery>>

    @Query("SELECT * FROM groceries WHERE daysToExpiry = 1")
    fun getExpiringTomorrow(): Flow<List<Grocery>>

    @Query("SELECT * FROM groceries WHERE type = :type")
    fun getGroceriesByType(type: String): Flow<List<Grocery>>

    @Query("SELECT * FROM groceries WHERE name = :name ORDER BY daysToExpiry ASC LIMIT 1")
    suspend fun getFirstExpiringGroceryByName(name: String): Grocery?

    @Query("SELECT * FROM groceries WHERE name = :name AND daysToExpiry = :daysToExpiry LIMIT 1")
    suspend fun getGroceryByNameAndExpiry(name: String, daysToExpiry: Int): Grocery?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groceries: List<Grocery>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(grocery: Grocery)

    @Update
    suspend fun update(grocery: Grocery)

    @Delete
    suspend fun delete(grocery: Grocery)

    @Query("SELECT COUNT(*) FROM groceries")
    suspend fun count(): Int
}
