package com.dessalines.thumbkey.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.db.AppSettingsViewModel
import com.dessalines.thumbkey.db.DEFAULT_WORD_COMBINATIONS_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_WORD_SUGGESTIONS_ENABLED
import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.utils.toBool
import com.dessalines.thumbkey.utils.toInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    var combinationsEnabled by remember(settings?.wordCombinationsEnabled) {
        mutableStateOf((settings?.wordCombinationsEnabled ?: DEFAULT_WORD_COMBINATIONS_ENABLED).toBool())
    }
    var query by remember { mutableStateOf("") }
    var wordToEdit by remember { mutableStateOf<WordUsage?>(null) }
    var wordToIgnore by remember { mutableStateOf<WordUsage?>(null) }
    var confirmReset by remember { mutableStateOf(false) }
    val wordList = rememberWordSuggestionListState(dao, query)

    Scaffold(
        topBar = {
            WordSuggestionsTopBar(navController)
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        ) {
            WordSuggestionsDescription()

            WordSuggestionSettings(
                enabled = enabled,
                combinationsEnabled = combinationsEnabled,
                onEnabledChange = {
                    enabled = it
                    appSettingsViewModel.updateWordSuggestionsEnabled(it.toInt())
                },
                onCombinationsEnabledChange = {
                    combinationsEnabled = it
                    appSettingsViewModel.updateWordCombinationsEnabled(it.toInt())
                },
            )

            WordSuggestionSearchField(query, onQueryChange = { query = it })
            ResetIgnoredWordsButton(wordList.ignoredCount, onClick = { confirmReset = true })
            WordSuggestionList(
                words = wordList.shown,
                allWords = wordList.words,
                actions =
                    WordSuggestionListActions(
                        onEdit = { wordToEdit = it },
                        onIgnore = { wordToIgnore = it },
                        onDelete = { word -> scope.launch(Dispatchers.IO) { dao.deleteById(word.id) } },
                    ),
            )
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
        IgnoreWordSuggestionDialog(
            word = word,
            onDismiss = { wordToIgnore = null },
            onConfirm = {
                scope.launch(Dispatchers.IO) { dao.ignoreById(word.id) }
                wordToIgnore = null
            },
        )
    }

    if (confirmReset) {
        ResetIgnoredWordsDialog(
            ignoredCount = wordList.ignoredCount,
            onDismiss = { confirmReset = false },
            onConfirm = {
                scope.launch(Dispatchers.IO) { dao.deleteIgnored() }
                confirmReset = false
            },
        )
    }
}
