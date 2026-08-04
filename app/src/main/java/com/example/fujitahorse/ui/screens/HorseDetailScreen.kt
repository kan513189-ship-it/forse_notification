package com.example.fujitahorse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fujitahorse.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HorseDetailScreen(viewModel: MainViewModel, horseId: String) {
    val entries by viewModel.entriesForHorse(horseId).collectAsState(initial = emptyList())
    val results by viewModel.resultsForHorse(horseId).collectAsState(initial = emptyList())
    val reports by viewModel.reportsForHorse(horseId).collectAsState(initial = emptyList())
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN) }

    var tabIndex by remember { mutableIntStateOf(0) }
    var showEntryDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    val tabs = listOf("予定", "結果", "次走報")

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (tabIndex) {
            0 -> {
                TextButton(onClick = { showEntryDialog = true }) { Text("＋ 出走予定を追加") }
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(entries) { entry ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(entry.raceName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${dateFormat.format(entry.raceDateMillis)} ${entry.venue}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (entry.memo.isNotBlank()) {
                                    Text(entry.memo, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                TextButton(onClick = { showResultDialog = true }) { Text("＋ レース結果を追加") }
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(results) { result ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(result.raceName, style = MaterialTheme.typography.titleMedium)
                                    Text(result.finishPosition, style = MaterialTheme.typography.titleMedium)
                                }
                                Text(
                                    "${dateFormat.format(result.raceDateMillis)} ${result.venue}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (result.memo.isNotBlank()) {
                                    Text(result.memo, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                TextButton(onClick = { showReportDialog = true }) { Text("＋ 次走報・メモを追加") }
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(reports) { report ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(report.targetRaceName, style = MaterialTheme.typography.titleMedium)
                                if (report.targetDateMillis != null) {
                                    Text(dateFormat.format(report.targetDateMillis), style = MaterialTheme.typography.bodySmall)
                                }
                                if (report.comment.isNotBlank()) {
                                    Text(report.comment, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEntryDialog) {
        AddEntryDialog(
            horseId = horseId,
            onDismiss = { showEntryDialog = false },
            onConfirm = { raceName, dateMillis, venue, memo ->
                viewModel.addRaceEntry(horseId, raceName, dateMillis, venue, memo)
                showEntryDialog = false
            }
        )
    }
    if (showResultDialog) {
        AddResultDialog(
            horseId = horseId,
            onDismiss = { showResultDialog = false },
            onConfirm = { raceName, dateMillis, venue, finish, memo ->
                viewModel.addRaceResult(horseId, raceName, dateMillis, venue, finish, memo)
                showResultDialog = false
            }
        )
    }
    if (showReportDialog) {
        AddNextReportDialog(
            horseId = horseId,
            onDismiss = { showReportDialog = false },
            onConfirm = { targetRaceName, targetDateMillis, comment ->
                viewModel.addNextRaceReport(horseId, targetRaceName, targetDateMillis, comment)
                showReportDialog = false
            }
        )
    }
}
