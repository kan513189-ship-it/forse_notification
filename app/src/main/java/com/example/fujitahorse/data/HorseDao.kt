package com.example.fujitahorse.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HorseDao {
    @Query("SELECT * FROM horses ORDER BY isActive DESC, name ASC")
    fun getAllHorses(): Flow<List<Horse>>

    @Query("SELECT * FROM horses WHERE horseId = :horseId")
    suspend fun getHorse(horseId: String): Horse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(horse: Horse)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(horses: List<Horse>)

    @Update
    suspend fun update(horse: Horse)

    @Delete
    suspend fun delete(horse: Horse)
}
