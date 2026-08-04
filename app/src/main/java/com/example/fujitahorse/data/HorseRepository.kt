package com.example.fujitahorse.data

import com.example.fujitahorse.network.NetkeibaScraper
import kotlinx.coroutines.flow.Flow

class HorseRepository(
    private val horseDao: HorseDao,
    private val entryDao: RaceEntryDao,
    private val resultDao: RaceResultDao,
    private val reportDao: NextRaceReportDao,
    private val scraper: NetkeibaScraper = NetkeibaScraper()
) {
    // --- Horse ---
    val allHorses: Flow<List<Horse>> = horseDao.getAllHorses()
    suspend fun upsertHorse(horse: Horse) = horseDao.upsert(horse)
    suspend fun deleteHorse(horse: Horse) = horseDao.delete(horse)

    // --- Race entries (予定) ---
    val allEntries: Flow<List<RaceEntry>> = entryDao.getAllEntries()
    fun upcomingEntries(fromMillis: Long) = entryDao.getUpcomingEntries(fromMillis)
    fun entriesForHorse(horseId: String) = entryDao.getEntriesForHorse(horseId)
    suspend fun addEntry(entry: RaceEntry) = entryDao.insert(entry)
    suspend fun updateEntry(entry: RaceEntry) = entryDao.update(entry)
    suspend fun deleteEntry(entry: RaceEntry) = entryDao.delete(entry)

    // --- Race results (結果) ---
    val allResults: Flow<List<RaceResult>> = resultDao.getAllResults()
    fun resultsForHorse(horseId: String) = resultDao.getResultsForHorse(horseId)
    suspend fun addResult(result: RaceResult) = resultDao.insert(result)
    suspend fun updateResult(result: RaceResult) = resultDao.update(result)
    suspend fun deleteResult(result: RaceResult) = resultDao.delete(result)

    // --- Next race report (次走報) ---
    val allReports: Flow<List<NextRaceReport>> = reportDao.getAllReports()
    fun reportsForHorse(horseId: String) = reportDao.getReportsForHorse(horseId)
    suspend fun addReport(report: NextRaceReport) = reportDao.insert(report)
    suspend fun deleteReport(report: NextRaceReport) = reportDao.delete(report)

    /**
     * netkeibaから藤田晋オーナーの馬リスト・今週の出走予定を取得しDBへ反映する。
     * 呼び出し側(WorkManagerのWorkerや手動更新ボタン)から実行される想定。
     */
    suspend fun syncFromNetkeiba(ownerId: String): SyncResult {
        val fetchedHorses = scraper.fetchOwnerHorseList(ownerId)
        if (fetchedHorses.isNotEmpty()) {
            horseDao.upsertAll(fetchedHorses)
        }

        val fetchedEntries = scraper.fetchOwnerThisWeekEntries(ownerId)
        if (fetchedEntries.isNotEmpty()) {
            entryDao.insertAllIfAbsent(fetchedEntries)
        }

        return SyncResult(
            horseCount = fetchedHorses.size,
            entryCount = fetchedEntries.size
        )
    }

    suspend fun getUnnotifiedFutureEntries(nowMillis: Long): List<RaceEntry> =
        entryDao.getUnnotifiedFuture(nowMillis)

    suspend fun markNotified(entry: RaceEntry) {
        entryDao.update(entry.copy(notified = true))
    }
}

data class SyncResult(val horseCount: Int, val entryCount: Int)
