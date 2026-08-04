package com.example.fujitahorse.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class EntrySource { AUTO, MANUAL }

/**
 * 出走予定(スケジュール)。自動取得(AUTO)または手動登録(MANUAL)。
 */
@Entity(
    tableName = "race_entries",
    foreignKeys = [
        ForeignKey(
            entity = Horse::class,
            parentColumns = ["horseId"],
            childColumns = ["horseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RaceEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val horseId: String,
    val raceDateMillis: Long,
    val raceName: String,
    val venue: String = "",
    val raceNumber: String = "",
    val memo: String = "",
    val notified: Boolean = false,
    val source: EntrySource = EntrySource.MANUAL
)
