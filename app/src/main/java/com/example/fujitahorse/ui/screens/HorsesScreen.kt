package com.example.fujitahorse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fujitahorse.ui.MainViewModel

@Composable
fun HorsesScreen(viewModel: MainViewModel, onHorseClick: (String) -> Unit) {
    val horses by viewModel.horses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "馬を追加")
            }
        }
    ) { padding ->
        if (horses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Text(
                    "追跡している馬はまだありません。右下の＋から追加するか、\n自動同期で藤田晋オーナー所有馬が反映されるのを待ってください。",
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(horses, key = { it.horseId }) { horse ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onHorseClick(horse.horseId) }
                    ) {
                        ListItem(
                            headlineContent = { Text(horse.name) },
                            supportingContent = {
                                Text(
                                    if (horse.sexAge.isNotBlank()) horse.sexAge else "馬ID: ${horse.horseId}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddHorseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, horseId ->
                viewModel.addHorse(name, horseId)
                showAddDialog = false
            }
        )
    }
}
