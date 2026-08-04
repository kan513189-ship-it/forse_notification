package com.example.fujitahorse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromEntrySource(value: EntrySource): String = value.name

    @TypeConverter
    fun toEntrySource(value: String): EntrySource = EntrySource.valueOf(value)
}

@Database(
    entities = [Horse::class, RaceEntry::class, RaceResult::class, NextRaceReport::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun horseDao(): HorseDao
    abstract fun raceEntryDao(): RaceEntryDao
    abstract fun raceResultDao(): RaceResultDao
    abstract fun nextRaceReportDao(): NextRaceReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fujita_horse_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
