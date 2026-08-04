package com.example.fujitahorse.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fujitahorse.R
import com.example.fujitahorse.data.RaceEntry

object NotificationHelper {
    const val CHANNEL_ID = "race_reminders"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "出走リマインダー",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "藤田晋オーナー馬の出走レース前に通知します"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun notifyUpcomingRace(context: Context, entry: RaceEntry, horseName: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("$horseName の出走が近づいています")
            .setContentText(entry.raceName.ifBlank { "レース予定あり" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(entry.id.toInt(), builder.build())
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS権限が未許可の場合はここに来る。呼び出し側でUI上許可を促す。
        }
    }
}
