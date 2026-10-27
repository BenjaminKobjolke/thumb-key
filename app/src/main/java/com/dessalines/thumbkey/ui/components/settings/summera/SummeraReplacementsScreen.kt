package com.dessalines.thumbkey.ui.components.settings.summera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.summera.SummeraAccount
import com.dessalines.thumbkey.summera.SummeraDictation
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import de.xida.aichatapi.TranscriptionReplacement
import de.xida.aichatapi.XidaAiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummeraReplacementsScreen(
    navController: NavController,
    transcript: String?,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentials = remember { SummeraAccount.credentials(ctx) }
    var replacements by remember { mutableStateOf<List<TranscriptionReplacement>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        credentials ?: return@LaunchedEffect
        try {
            replacements =
                withContext(Dispatchers.IO) {
                    SummeraAccount.client(ctx).settings.getTranscriptionReplacements(credentials)
                }
        } catch (e: XidaAiException) {
            error = e.message.orEmpty()
        }
    }

    fun save(
        list: List<TranscriptionReplacement>,
        onSaved: () -> Unit = {},
    ) {
        credentials ?: return
        error = null
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    SummeraAccount.client(ctx).settings.saveTranscriptionReplacements(credentials, list)
                }
                replacements = list
                SummeraDictation.onReplacementsSaved(list)
                onSaved()
            } catch (e: XidaAiException) {
                error = e.message.orEmpty()
            }
        }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.summera_replacements),
                navController = navController,
            )
        },
        content = { padding ->
            ReplacementsContent(
                modifier = Modifier.padding(padding),
                state =
                    ReplacementContentState(
                        credentialsAvailable = credentials != null,
                        transcript = transcript,
                        replacements = replacements,
                        error = error,
                        from = from,
                        to = to,
                        onFromChange = { from = it },
                        onToChange = { to = it },
                        onSave = ::save,
                    ),
            )
        },
    )
}

private data class ReplacementContentState(
    val credentialsAvailable: Boolean,
    val transcript: String?,
    val replacements: List<TranscriptionReplacement>?,
    val error: String?,
    val from: String,
    val to: String,
    val onFromChange: (String) -> Unit,
    val onToChange: (String) -> Unit,
    val onSave: (List<TranscriptionReplacement>, () -> Unit) -> Unit,
)

@Composable
private fun ReplacementsContent(
    modifier: Modifier,
    state: ReplacementContentState,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier =
            modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 12.dp),
    ) {
        ProvidePreferenceTheme {
            Text(
                text = stringResource(R.string.summera_replacements_hint),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            state.error?.let { Preference(title = { Text(stringResource(R.string.summera_failed, it)) }) }
            if (!state.credentialsAvailable) {
                Preference(title = { Text(stringResource(R.string.summera_sign_in_first)) })
            } else if (state.replacements == null) {
                if (state.error == null) LoadingReplacements()
            } else {
                ReplacementListContent(state, state.replacements)
            }
        }
    }
}

@Composable
private fun LoadingReplacements() {
    Preference(
        title = { Text(stringResource(R.string.summera_loading)) },
        icon = { CircularProgressIndicator() },
    )
}

@Composable
private fun ReplacementListContent(
    state: ReplacementContentState,
    replacements: List<TranscriptionReplacement>,
) {
    if (!state.transcript.isNullOrBlank()) {
        TranscriptWordChips(state.transcript, state.from, state.onFromChange)
    }
    ReplacementEditor(state, replacements)
    replacements.forEach { replacement ->
        SavedReplacement(replacement, replacements, state)
    }
}

@Composable
private fun ReplacementEditor(
    state: ReplacementContentState,
    replacements: List<TranscriptionReplacement>,
) {
    OutlinedTextField(
        value = state.from,
        onValueChange = state.onFromChange,
        label = { Text(stringResource(R.string.summera_replacement_from)) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    )
    OutlinedTextField(
        value = state.to,
        onValueChange = state.onToChange,
        label = { Text(stringResource(R.string.summera_replacement_to)) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    )
    Button(
        enabled = state.from.isNotBlank() && state.to.isNotBlank(),
        onClick = {
            val saved = TranscriptionReplacement(state.from.trim(), state.to.trim())
            state.onSave(replacements.filterNot { it.from.equals(saved.from, ignoreCase = true) } + saved) {
                state.onFromChange("")
                state.onToChange("")
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Text(stringResource(R.string.summera_save))
    }
}

@Composable
private fun SavedReplacement(
    replacement: TranscriptionReplacement,
    replacements: List<TranscriptionReplacement>,
    state: ReplacementContentState,
) {
    Preference(
        title = { Text("${replacement.from} → ${replacement.to}") },
        widgetContainer = {
            IconButton(onClick = { state.onSave(replacements - replacement) {} }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.summera_delete_replacement),
                )
            }
        },
        onClick = {
            state.onFromChange(replacement.from)
            state.onToChange(replacement.to)
        },
    )
}

@Composable
private fun TranscriptWordChips(
    transcript: String,
    selectedWord: String,
    onPick: (String) -> Unit,
) {
    Text(
        text = stringResource(R.string.summera_replacements_pick_word),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        transcript
            .split(Regex("\\s+"))
            .map { it.trim { character -> !character.isLetterOrDigit() } }
            .filter { it.isNotBlank() }
            .forEach { word ->
                FilterChip(
                    selected = selectedWord == word,
                    onClick = { onPick(word) },
                    label = { Text(word) },
                )
            }
    }
}
