package com.dessalines.thumbkey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.db.AppSettingsViewModel
import com.dessalines.thumbkey.db.DEFAULT_WORD_SUGGESTIONS_ENABLED
import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import com.dessalines.thumbkey.utils.fuzzyRank
import com.dessalines.thumbkey.utils.toBool
import com.dessalines.thumbkey.utils.toInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SwitchPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordSuggestionsScreen(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
) {
    val context = LocalContext.current
    val dao = remember { AppDB.getDatabase(context).wordUsageDao() }
    val scope = rememberCoroutineScope()
    val settings by appSettingsViewModel.appSettings.observeAsState()
    var enabled by remember(settings?.wordSuggestionsEnabled) {
        mutableStateOf((settings?.wordSuggestionsEnabled ?: DEFAULT_WORD_SUGGESTIONS_ENABLED).toBool())
    }
    var query by remember { mutableStateOf("") }
    var wordToEdit by remember { mutableStateOf<WordUsage?>(null) }
    var wordToIgnore by remember { mutableStateOf<WordUsage?>(null) }
    var confirmReset by remember { mutableStateOf(false) }
    val words by remember(dao) { dao.all() }.collectAsState(initial = emptyList())
    val ignoredCount by remember(dao) { dao.ignoredCount() }.collectAsState(initial = 0)
    val shown =
        remember(words, query) {
            words
                .mapNotNull { word -> fuzzyRank(query, word.word)?.let { it to word } }
                .sortedBy { it.first }
                .map { it.second }
        }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.word_suggestions_title),
                navController = navController,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.word_suggestions_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            ProvidePreferenceTheme {
                SwitchPreference(
                    value = enabled,
                    onValueChange = {
                        enabled = it
                        appSettingsViewModel.updateWordSuggestionsEnabled(it.toInt())
                    },
                    title = { Text(stringResource(R.string.word_suggestions)) },
                    summary = { Text(stringResource(R.string.word_suggestions_summary)) },
                )
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                label = { Text(stringResource(R.string.word_suggestions_search)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear_search))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
            )

            if (ignoredCount > 0) {
                TextButton(onClick = { confirmReset = true }) {
                    Text(stringResource(R.string.word_suggestions_reset_ignored, ignoredCount))
                }
            }

            if (shown.isEmpty()) {
                Text(
                    text = stringResource(if (words.isEmpty()) R.string.word_suggestions_empty else R.string.word_suggestions_no_match),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    items(shown, key = { it.id }) { word ->
                        WordSuggestionItem(
                            word = word,
                            onEdit = { wordToEdit = word },
                            onIgnore = { wordToIgnore = word },
                            onDelete = { scope.launch(Dispatchers.IO) { dao.deleteById(word.id) } },
                        )
                    }
                }
            }
        }
    }

    wordToEdit?.let { word ->
        EditWordSuggestionDialog(
            word = word,
            onDismiss = { wordToEdit = null },
            onSave = { value ->
                scope.launch(Dispatchers.IO) {
                    val cleaned = value.trim()
                    if (cleaned.isBlank()) {
                        dao.deleteById(word.id)
                    } else {
                        dao.rename(word.id, cleaned, cleaned.lowercase())
                    }
                }
                wordToEdit = null
            },
        )
    }

    wordToIgnore?.let { word ->
        AlertDialog(
            onDismissRequest = { wordToIgnore = null },
            title = { Text(stringResource(R.string.word_suggestion_ignore_confirm_title)) },
            text = { Text(stringResource(R.string.word_suggestion_ignore_confirm, word.word)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) { dao.ignoreById(word.id) }
                        wordToIgnore = null
                    },
                ) {
                    Text(stringResource(R.string.word_suggestion_ignore))
                }
            },
            dismissButton = {
                TextButton(onClick = { wordToIgnore = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            text = { Text(stringResource(R.string.word_suggestions_reset_ignored_confirm, ignoredCount)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) { dao.deleteIgnored() }
                        confirmReset = false
                    },
                ) {
                    Text(stringResource(R.string.word_suggestions_reset))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordSuggestionItem(
    word: WordUsage,
    onEdit: () -> Unit,
    onIgnore: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        onClick = onEdit,
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(word.word, style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(R.string.word_suggestions_used_count, word.useCount),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Row {
                IconButton(onClick = onIgnore) {
                    Icon(Icons.Default.Block, contentDescription = stringResource(R.string.word_suggestion_ignore))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.word_suggestion_delete))
                }
            }
        }
    }
}

@Composable
private fun EditWordSuggestionDialog(
    word: WordUsage,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var value by remember(word.id) { mutableStateOf(word.word) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.word_suggestion_edit)) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text(stringResource(R.string.word_suggestion_edit_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(value) }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
