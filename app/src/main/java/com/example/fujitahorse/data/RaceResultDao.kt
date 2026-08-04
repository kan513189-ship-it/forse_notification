package com.example.fujitahorse.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceResultDao {
    @Query("SELECT * FROM race_results ORDER BY raceDateMillis DESC")
    fun getAllResults(): Flow<List<RaceResult>>

    @Query("SELECT * FROM race_results WHERE horseId = :horseId ORDER BY raceDateMillis DESC")
    fun getResultsForHorse(horseId: String): Flow<List<RaceResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: RaceResult): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIfAbsent(results: List<RaceResult>)

    @Update
    suspend fun update(result: RaceResult)

    @Delete
    suspend fun delete(result: RaceResult)
}
