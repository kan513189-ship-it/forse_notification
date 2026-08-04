package com.example.fujitahorse.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 次走報: 「次はこのレースを目標にしている」といった調教師コメント・見込み情報のメモ。
 */
@Entity(
    tableName = "next_race_reports",
    foreignKeys = [
        ForeignKey(
            entity = Horse::class,
            parentColumns = ["horseId"],
            childColumns = ["horseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NextRaceReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val horseId: String,
    val createdAtMillis: Long,
    val targetRaceName: String,
    val targetDateMillis: Long? = null,
    val comment: String = ""
)
