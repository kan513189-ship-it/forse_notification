package com.example.fujitahorse.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fujitahorse.data.AppDatabase
import com.example.fujitahorse.data.Horse
import com.example.fujitahorse.data.HorseRepository
import com.example.fujitahorse.notification.NotificationHelper

/** 藤田晋オーナーのnetkeiba馬主ID */
const val OWNER_ID = "232031"

/** 出走何時間前に通知するか */
private const val REMINDER_WINDOW_HOURS = 24L

class SyncWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getDatabase(applicationContext)
            val repository = HorseRepository(
                db.horseDao(), db.raceEntryDao(), db.raceResultDao(), db.nextRaceReportDao()
            )

            repository.syncFromNetkeiba(OWNER_ID)
            sendDueReminders(repository)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun sendDueReminders(repository: HorseRepository) {
        val now = System.currentTimeMillis()
        val windowEnd = now + REMINDER_WINDOW_HOURS * 60 * 60 * 1000
        val candidates = repository.getUnnotifiedFutureEntries(now)
            .filter { it.raceDateMillis in now..windowEnd }

        for (entry in candidates) {
            val horseName = getHorseName(entry.horseId)
            NotificationHelper.notifyUpcomingRace(applicationContext, entry, horseName)
            repository.markNotified(entry)
        }
    }

    private suspend fun getHorseName(horseId: String): String {
        val db = AppDatabase.getDatabase(applicationContext)
        val horse: Horse? = db.horseDao().getHorse(horseId)
        return horse?.name ?: "登録馬"
    }
}
