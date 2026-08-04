package com.example.fujitahorse.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NextRaceReportDao {
    @Query("SELECT * FROM next_race_reports ORDER BY createdAtMillis DESC")
    fun getAllReports(): Flow<List<NextRaceReport>>

    @Query("SELECT * FROM next_race_reports WHERE horseId = :horseId ORDER BY createdAtMillis DESC")
    fun getReportsForHorse(horseId: String): Flow<List<NextRaceReport>>

    @Insert
    suspend fun insert(report: NextRaceReport): Long

    @Update
    suspend fun update(report: NextRaceReport)

    @Delete
    suspend fun delete(report: NextRaceReport)
}
