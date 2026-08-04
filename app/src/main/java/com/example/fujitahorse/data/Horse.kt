package com.example.fujitahorse.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 追跡対象の馬。netkeibaの馬ID(例: 2021110048)をキーとして保持する。
 */
@Entity(tableName = "horses")
data class Horse(
    @PrimaryKey
    val horseId: String,
    val name: String,
    val sexAge: String = "",
    val isActive: Boolean = true,
    val memo: String = "",
    val lastSyncedAt: Long = 0L
)
