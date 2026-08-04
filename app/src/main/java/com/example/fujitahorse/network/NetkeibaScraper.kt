package com.example.fujitahorse.network

import com.example.fujitahorse.data.EntrySource
import com.example.fujitahorse.data.Horse
import com.example.fujitahorse.data.RaceEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.Calendar

/**
 * netkeiba.comの公開ページ(ログイン不要)をパースするスクレイパー。
 *
 * 注意:
 * - netkeibaはスクレイピングを明示的に禁止してはいないが、短時間に大量アクセスすると
 *   通信制限がかかる場合がある。呼び出し側(SyncWorker等)は頻度を抑えること(1日数回程度を推奨)。
 * - netkeibaのHTML構造は将来変更される可能性がある。取得に失敗する場合はセレクタの
 *   見直しが必要(ブラウザの開発者ツールで最新のclass名/構造を確認してください)。
 * - ここでのCSSセレクタは典型的なnetkeibaのテーブル構造を想定した実装例です。
 */
class NetkeibaScraper {

    private val userAgent =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/125.0.0.0 Safari/537.36 FujitaHorseApp/1.0 (personal-use schedule tracker)"

    private suspend fun fetchDocument(url: String): Document? = withContext(Dispatchers.IO) {
        try {
            Jsoup.connect(url)
                .userAgent(userAgent)
                .timeout(15000)
                .get()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 馬主(owner_id)の所有馬一覧を取得する。
     * 例: https://db.netkeiba.com/horse/list.html?owner_id=232031
     */
    suspend fun fetchOwnerHorseList(ownerId: String): List<Horse> {
        val url = "https://db.netkeiba.com/horse/list.html?owner_id=$ownerId"
        val doc = fetchDocument(url) ?: return emptyList()

        val horses = mutableListOf<Horse>()
        // 馬名へのリンクは /horse/{horseId}/ の形式。ページ内の該当リンクを総なめして抽出する。
        val links = doc.select("a[href*=/horse/]")
        val horseIdRegex = Regex("/horse/(\\d{10})/?")

        for (link in links) {
            val href = link.attr("href")
            val match = horseIdRegex.find(href) ?: continue
            val horseId = match.groupValues[1]
            val name = link.text().trim()
            if (name.isBlank()) continue
            if (horses.any { it.horseId == horseId }) continue

            horses.add(
                Horse(
                    horseId = horseId,
                    name = name,
                    lastSyncedAt = System.currentTimeMillis()
                )
            )
        }
        return horses
    }

    /**
     * 馬主の「今週の出走馬」情報を取得する。
     * 例: https://db.netkeiba.com/owner/thisweek/232031/
     *
     * ページ内のテーブル行から「日付・レース名・馬名」を推定して抽出する。
     * 実際の列構成が異なる場合は、取得後にコンソール等でHTML(doc)を確認し
     * セレクタを調整すること。
     */
    suspend fun fetchOwnerThisWeekEntries(ownerId: String): List<RaceEntry> {
        val url = "https://db.netkeiba.com/owner/thisweek/$ownerId/"
        val doc = fetchDocument(url) ?: return emptyList()

        val entries = mutableListOf<RaceEntry>()
        val horseIdRegex = Regex("/horse/(\\d{10})/?")
        val dateRegex = Regex("(\\d{1,2})/(\\d{1,2})")

        // テーブル行を走査し、馬リンクを含む行を「出走予定」として扱う
        val rows = doc.select("table tr")
        for (row in rows) {
            val horseLink = row.selectFirst("a[href*=/horse/]") ?: continue
            val match = horseIdRegex.find(horseLink.attr("href")) ?: continue
            val horseId = match.groupValues[1]

            val rowText = row.text()
            val dateMatch = dateRegex.find(rowText)
            val raceDateMillis = if (dateMatch != null) {
                parseUpcomingDate(dateMatch.groupValues[1].toInt(), dateMatch.groupValues[2].toInt())
            } else {
                null
            }

            // レース名らしきリンク(/race/を含む)があれば使用、なければ行テキストをそのまま保存
            val raceLink = row.selectFirst("a[href*=/race/]")
            val raceName = raceLink?.text()?.trim().takeUnless { it.isNullOrBlank() }
                ?: rowText.trim().take(50)

            entries.add(
                RaceEntry(
                    horseId = horseId,
                    raceDateMillis = raceDateMillis ?: System.currentTimeMillis(),
                    raceName = raceName,
                    source = EntrySource.AUTO
                )
            )
        }
        return entries
    }

    /** 月/日だけの表記から、直近の未来日付のミリ秒を推定する */
    private fun parseUpcomingDate(month: Int, day: Int): Long {
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        cal.set(currentYear, month - 1, day, 10, 0, 0)
        if (cal.timeInMillis < System.currentTimeMillis() - 24L * 60 * 60 * 1000) {
            // すでに過ぎた日付なら来年とみなす
            cal.set(currentYear + 1, month - 1, day, 10, 0, 0)
        }
        return cal.timeInMillis
    }
}
