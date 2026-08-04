package com.example.fujitahorse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fujitahorse.data.EntrySource
import com.example.fujitahorse.data.Horse
import com.example.fujitahorse.data.RaceEntry
import com.example.fujitahorse.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val entries by viewModel.upcomingEntries.collectAsState()
    val horses by viewModel.horses.collectAsState()
    val dateFormat = rememberDateFormat()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("直近の出走予定", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { viewModel.manualSync() }) {
                Text("今すぐ更新")
            }
        }

        if (entries.isEmpty()) {
            Text(
                "出走予定はまだありません。\n自動同期(1日数回)を待つか、馬詳細画面から手動登録してください。",
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    val horseName = horses.firstOrNull { it.horseId == entry.horseId }?.name
                        ?: entry.horseId
                    EntryRow(entry, horseName, dateFormat)
                }
            }
        }
    }
}

@Composable
private fun EntryRow(entry: RaceEntry, horseName: String, dateFormat: SimpleDateFormat) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(horseName, style = MaterialTheme.typography.titleMedium)
            Text(entry.raceName, style = MaterialTheme.typography.bodyLarge)
            val venueText = if (entry.venue.isNotBlank()) " / ${entry.venue}" else ""
            Text(
                "${dateFormat.format(entry.raceDateMillis)}$venueText" +
                    if (entry.source == EntrySource.AUTO) " (自動取得)" else " (手動登録)",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun rememberDateFormat(): SimpleDateFormat =
    androidx.compose.runtime.remember { SimpleDateFormat("MM/dd(E) HH:mm", Locale.JAPAN) }
