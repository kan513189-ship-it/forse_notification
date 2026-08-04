package com.example.fujitahorse.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceEntryDao {
    @Query("SELECT * FROM race_entries ORDER BY raceDateMillis ASC")
    fun getAllEntries(): Flow<List<RaceEntry>>

    @Query("SELECT * FROM race_entries WHERE raceDateMillis >= :fromMillis ORDER BY raceDateMillis ASC")
    fun getUpcomingEntries(fromMillis: Long): Flow<List<RaceEntry>>

    @Query("SELECT * FROM race_entries WHERE horseId = :horseId ORDER BY raceDateMillis DESC")
    fun getEntriesForHorse(horseId: String): Flow<List<RaceEntry>>

    @Query("SELECT * FROM race_entries WHERE notified = 0 AND raceDateMillis >= :nowMillis")
    suspend fun getUnnotifiedFuture(nowMillis: Long): List<RaceEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: RaceEntry): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIfAbsent(entries: List<RaceEntry>)

    @Update
    suspend fun update(entry: RaceEntry)

    @Delete
    suspend fun delete(entry: RaceEntry)
}
