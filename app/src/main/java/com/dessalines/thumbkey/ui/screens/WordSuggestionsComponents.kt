@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.dessalines.thumbkey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.db.WordUsageDao
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import com.dessalines.thumbkey.utils.fuzzyRank
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import me.zhanghai.compose.preference.SwitchPreference

internal data class WordSuggestionListState(
    val words: List<WordUsage>,
    val ignoredCount: Int,
    val shown: List<WordUsage>,
)

@Composable
internal fun WordSuggestionsDescription() {
    Text(
        text = stringResource(R.string.word_suggestions_description),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 16.dp),
    )
}

@Composable
internal fun WordSuggestionsTopBar(navController: NavController) {
    SimpleTopAppBar(
        text = stringResource(R.string.word_suggestions_title),
        navController = navController,
    )
}

@Composable
internal fun rememberWordSuggestionListState(
    dao: WordUsageDao,
    query: String,
): WordSuggestionListState {
    val words by remember(dao) { dao.all() }.collectAsState(initial = emptyList())
    val ignoredCount by remember(dao) { dao.ignoredCount() }.collectAsState(initial = 0)
    val shown =
        remember(words, query) {
            words
                .mapNotNull { word -> fuzzyRank(query, word.word)?.let { it to word } }
                .sortedBy { it.first }
                .map { it.second }
        }
    return WordSuggestionListState(words, ignoredCount, shown)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun WordSuggestionSettings(
    enabled: Boolean,
    combinationsEnabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onCombinationsEnabledChange: (Boolean) -> Unit,
) {
    ProvidePreferenceTheme {
        SwitchPreference(
            value = enabled,
            onValueChange = onEnabledChange,
            title = { Text(stringResource(R.string.word_suggestions)) },
            summary = { Text(stringResource(R.string.word_suggestions_summary)) },
        )
        SwitchPreference(
            value = combinationsEnabled,
            onValueChange = onCombinationsEnabledChange,
            title = { Text(stringResource(R.string.word_combinations)) },
            summary = { Text(stringResource(R.string.word_combinations_summary)) },
            enabled = enabled,
        )
    }
}

@Composable
internal fun WordSuggestionSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        label = { Text(stringResource(R.string.word_suggestions_search)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear_search))
                }
            }
        },
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
    )
}

@Composable
internal fun ResetIgnoredWordsButton(
    ignoredCount: Int,
    onClick: () -> Unit,
) {
    if (ignoredCount > 0) {
        TextButton(onClick = onClick) {
            Text(stringResource(R.string.word_suggestions_reset_ignored, ignoredCount))
        }
    }
}

@Composable
internal fun WordSuggestionList(
    words: List<WordUsage>,
    allWords: List<WordUsage>,
    actions: WordSuggestionListActions,
) {
    if (words.isEmpty()) {
        Text(
            text = stringResource(if (allWords.isEmpty()) R.string.word_suggestions_empty else R.string.word_suggestions_no_match),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp),
        )
    } else {
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            items(words, key = { it.id }) { word ->
                WordSuggestionItem(
                    word = word,
                    onEdit = { actions.onEdit(word) },
                    onIgnore = { actions.onIgnore(word) },
                    onDelete = { actions.onDelete(word) },
                )
            }
        }
    }
}

internal data class WordSuggestionListActions(
    val onEdit: (WordUsage) -> Unit,
    val onIgnore: (WordUsage) -> Unit,
    val onDelete: (WordUsage) -> Unit,
)

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
internal fun EditWordSuggestionDialog(
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

@Composable
internal fun IgnoreWordSuggestionDialog(
    word: WordUsage,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.word_suggestion_ignore_confirm_title)) },
        text = { Text(stringResource(R.string.word_suggestion_ignore_confirm, word.word)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.word_suggestion_ignore))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
internal fun ResetIgnoredWordsDialog(
    ignoredCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(stringResource(R.string.word_suggestions_reset_ignored_confirm, ignoredCount)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.word_suggestions_reset))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
