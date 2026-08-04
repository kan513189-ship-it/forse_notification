package com.example.fujitahorse

import android.app.Application
import com.example.fujitahorse.notification.NotificationHelper
import com.example.fujitahorse.work.SyncScheduler

class FujitaHorseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        // netkeibaへの負荷を抑えるため12時間間隔を既定値とする
        SyncScheduler.schedulePeriodicSync(this, intervalHours = 12)
    }
}
