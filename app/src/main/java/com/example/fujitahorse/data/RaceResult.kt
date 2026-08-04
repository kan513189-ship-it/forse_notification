package com.example.fujitahorse.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * レース結果の記録。自動取得または手動入力。
 */
@Entity(
    tableName = "race_results",
    foreignKeys = [
        ForeignKey(
            entity = Horse::class,
            parentColumns = ["horseId"],
            childColumns = ["horseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RaceResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val horseId: String,
    val raceDateMillis: Long,
    val raceName: String,
    val venue: String = "",
    val finishPosition: String = "",
    val jockey: String = "",
    val prizeMoney: String = "",
    val memo: String = "",
    val source: EntrySource = EntrySource.MANUAL
)
