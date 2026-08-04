package com.example.fujitahorse.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN)

@Composable
fun AddHorseDialog(onDismiss: () -> Unit, onConfirm: (name: String, horseId: String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var horseId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("追跡する馬を追加") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("馬名") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = horseId,
                    onValueChange = { horseId = it },
                    label = { Text("netkeiba馬ID (例: 2021110048)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                Text(
                    "馬IDはnetkeibaの馬プロフィールURL末尾の数字です",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, horseId) }) { Text("追加") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}

@Composable
fun AddEntryDialog(
    horseId: String,
    onDismiss: () -> Unit,
    onConfirm: (raceName: String, dateMillis: Long, venue: String, memo: String) -> Unit
) {
    var raceName by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(dateFormat.format(Date())) }
    var venue by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("出走予定を登録") },
        text = {
            Column {
                OutlinedTextField(
                    value = raceName, onValueChange = { raceName = it },
                    label = { Text("レース名") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateText, onValueChange = { dateText = it },
                    label = { Text("日付 (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = venue, onValueChange = { venue = it },
                    label = { Text("競馬場") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = memo, onValueChange = { memo = it },
                    label = { Text("メモ") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val millis = runCatching { dateFormat.parse(dateText)?.time }.getOrNull()
                    ?: System.currentTimeMillis()
                onConfirm(raceName, millis, venue, memo)
            }) { Text("登録") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}

@Composable
fun AddResultDialog(
    horseId: String,
    onDismiss: () -> Unit,
    onConfirm: (raceName: String, dateMillis: Long, venue: String, finish: String, memo: String) -> Unit
) {
    var raceName by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(dateFormat.format(Date())) }
    var venue by remember { mutableStateOf("") }
    var finish by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("レース結果を記録") },
        text = {
            Column {
                OutlinedTextField(
                    value = raceName, onValueChange = { raceName = it },
                    label = { Text("レース名") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateText, onValueChange = { dateText = it },
                    label = { Text("日付 (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = venue, onValueChange = { venue = it },
                    label = { Text("競馬場") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = finish, onValueChange = { finish = it },
                    label = { Text("着順 (例: 1着)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = memo, onValueChange = { memo = it },
                    label = { Text("メモ") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val millis = runCatching { dateFormat.parse(dateText)?.time }.getOrNull()
                    ?: System.currentTimeMillis()
                onConfirm(raceName, millis, venue, finish, memo)
            }) { Text("登録") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}

@Composable
fun AddNextReportDialog(
    horseId: String,
    onDismiss: () -> Unit,
    onConfirm: (targetRaceName: String, targetDateMillis: Long?, comment: String) -> Unit
) {
    var targetRaceName by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("次走報・メモを登録") },
        text = {
            Column {
                OutlinedTextField(
                    value = targetRaceName, onValueChange = { targetRaceName = it },
                    label = { Text("目標レース") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dateText, onValueChange = { dateText = it },
                    label = { Text("予定日 (yyyy-MM-dd、任意)") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = comment, onValueChange = { comment = it },
                    label = { Text("コメント") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val millis = if (dateText.isBlank()) null else
                    runCatching { dateFormat.parse(dateText)?.time }.getOrNull()
                onConfirm(targetRaceName, millis, comment)
            }) { Text("登録") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}
