package com.example.fujitahorse.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fujitahorse.data.AppDatabase
import com.example.fujitahorse.data.EntrySource
import com.example.fujitahorse.data.Horse
import com.example.fujitahorse.data.HorseRepository
import com.example.fujitahorse.data.NextRaceReport
import com.example.fujitahorse.data.RaceEntry
import com.example.fujitahorse.data.RaceResult
import com.example.fujitahorse.work.OWNER_ID
import com.example.fujitahorse.work.SyncScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HorseRepository

    val horses: StateFlow<List<Horse>>
    val upcomingEntries: StateFlow<List<RaceEntry>>
    val allResults: StateFlow<List<RaceResult>>

    var isSyncing = false
        private set
    var lastSyncMessage: String? = null
        private set

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HorseRepository(
            db.horseDao(), db.raceEntryDao(), db.raceResultDao(), db.nextRaceReportDao()
        )

        horses = repository.allHorses.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        upcomingEntries = repository.upcomingEntries(System.currentTimeMillis()).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allResults = repository.allResults.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
    }

    fun manualSync() {
        SyncScheduler.triggerManualSync(getApplication())
    }

    fun addHorse(name: String, horseId: String) {
        if (name.isBlank() || horseId.isBlank()) return
        viewModelScope.launch {
            repository.upsertHorse(Horse(horseId = horseId.trim(), name = name.trim()))
        }
    }

    fun deleteHorse(horse: Horse) {
        viewModelScope.launch { repository.deleteHorse(horse) }
    }

    fun addRaceEntry(horseId: String, raceName: String, dateMillis: Long, venue: String, memo: String) {
        if (horseId.isBlank() || raceName.isBlank()) return
        viewModelScope.launch {
            repository.addEntry(
                RaceEntry(
                    horseId = horseId,
                    raceDateMillis = dateMillis,
                    raceName = raceName,
                    venue = venue,
                    memo = memo,
                    source = EntrySource.MANUAL
                )
            )
        }
    }

    fun deleteEntry(entry: RaceEntry) {
        viewModelScope.launch { repository.deleteEntry(entry) }
    }

    fun addRaceResult(
        horseId: String,
        raceName: String,
        dateMillis: Long,
        venue: String,
        finishPosition: String,
        memo: String
    ) {
        if (horseId.isBlank() || raceName.isBlank()) return
        viewModelScope.launch {
            repository.addResult(
                RaceResult(
                    horseId = horseId,
                    raceDateMillis = dateMillis,
                    raceName = raceName,
                    venue = venue,
                    finishPosition = finishPosition,
                    memo = memo,
                    source = EntrySource.MANUAL
                )
            )
        }
    }

    fun deleteResult(result: RaceResult) {
        viewModelScope.launch { repository.deleteResult(result) }
    }

    fun resultsForHorse(horseId: String) = repository.resultsForHorse(horseId)
    fun entriesForHorse(horseId: String) = repository.entriesForHorse(horseId)
    fun reportsForHorse(horseId: String) = repository.reportsForHorse(horseId)

    fun addNextRaceReport(horseId: String, targetRaceName: String, targetDateMillis: Long?, comment: String) {
        if (horseId.isBlank() || targetRaceName.isBlank()) return
        viewModelScope.launch {
            repository.addReport(
                NextRaceReport(
                    horseId = horseId,
                    createdAtMillis = System.currentTimeMillis(),
                    targetRaceName = targetRaceName,
                    targetDateMillis = targetDateMillis,
                    comment = comment
                )
            )
        }
    }

    fun deleteReport(report: NextRaceReport) {
        viewModelScope.launch { repository.deleteReport(report) }
    }

    companion object {
        const val DEFAULT_OWNER_ID = OWNER_ID
    }
}
