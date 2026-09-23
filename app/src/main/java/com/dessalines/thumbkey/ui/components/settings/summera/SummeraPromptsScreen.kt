package com.dessalines.thumbkey.ui.components.settings.summera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.summera.SummeraAccount
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import de.xida.aichatapi.TranscriptionPrompt
import de.xida.aichatapi.XidaAiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/** Manages the saved prompts of the `transcriptionPrompts` user setting and picks the active one. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummeraPromptsScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var prompts by remember { mutableStateOf<List<TranscriptionPrompt>?>(null) }
    var activeId by remember { mutableStateOf(SummeraAccount.activePromptId(ctx)) }
    var error by remember { mutableStateOf<String?>(null) }
    // null: no dialog; a prompt with a blank id: the add dialog
    var editing by remember { mutableStateOf<TranscriptionPrompt?>(null) }

    fun setActive(prompt: TranscriptionPrompt?) {
        SummeraAccount.saveActivePrompt(ctx, prompt)
        activeId = prompt?.id
    }

    LaunchedEffect(Unit) {
        val credentials = SummeraAccount.credentials(ctx) ?: return@LaunchedEffect
        try {
            val loaded = withContext(Dispatchers.IO) { SummeraAccount.client(ctx).settings.getTranscriptionPrompts(credentials) }
            prompts = loaded
            // Deleted on another device
            if (activeId != null && loaded.none { it.id == activeId }) setActive(null)
        } catch (e: XidaAiException) {
            error = e.message.orEmpty()
        }
    }

    fun save(list: List<TranscriptionPrompt>) {
        val credentials = SummeraAccount.credentials(ctx) ?: return
        error = null
        scope.launch {
            try {
                withContext(Dispatchers.IO) { SummeraAccount.client(ctx).settings.saveTranscriptionPrompts(credentials, list) }
                prompts = list
            } catch (e: XidaAiException) {
                error = e.message.orEmpty()
            }
        }
    }

    editing?.let { current ->
        PromptDialog(
            initialText = current.text,
            onDismiss = { editing = null },
            onSave = { text ->
                editing = null
                val now = utcNow()
                val list = prompts.orEmpty()
                val saved =
                    if (current.id.isEmpty()) {
                        TranscriptionPrompt(id = UUID.randomUUID().toString(), text = text, createdAt = now, updatedAt = now)
                    } else {
                        current.copy(text = text, updatedAt = now)
                    }
                save(if (current.id.isEmpty()) list + saved else list.map { if (it.id == saved.id) saved else it })
                if (saved.id == activeId) setActive(saved)
            },
        )
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.summera_prompts),
                navController = navController,
            )
        },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .background(color = MaterialTheme.colorScheme.surface),
            ) {
                ProvidePreferenceTheme {
                    error?.let {
                        Preference(title = { Text(stringResource(R.string.summera_failed, it)) })
                    }
                    val list = prompts
                    if (list == null) {
                        if (error == null) {
                            Preference(
                                title = { Text(stringResource(R.string.summera_loading)) },
                                icon = { CircularProgressIndicator() },
                            )
                        }
                        return@ProvidePreferenceTheme
                    }
                    Preference(
                        title = { Text(stringResource(R.string.summera_no_prompt)) },
                        icon = { RadioButton(selected = activeId == null, onClick = null) },
                        onClick = { setActive(null) },
                    )
                    list.forEach { prompt ->
                        Preference(
                            title = { Text(prompt.text) },
                            icon = { RadioButton(selected = activeId == prompt.id, onClick = null) },
                            widgetContainer = {
                                Row {
                                    IconButton(onClick = { editing = prompt }) {
                                        Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.summera_edit_prompt))
                                    }
                                    IconButton(
                                        onClick = {
                                            save(list.filter { it.id != prompt.id })
                                            if (prompt.id == activeId) setActive(null)
                                        },
                                    ) {
                                        Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.summera_delete_prompt))
                                    }
                                }
                            },
                            onClick = { setActive(prompt) },
                        )
                    }
                    Preference(
                        title = { Text(stringResource(R.string.summera_add_prompt)) },
                        icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                        onClick = { editing = TranscriptionPrompt(id = "", text = "", createdAt = "", updatedAt = "") },
                    )
                }
            }
        },
    )
}

@Composable
private fun PromptDialog(
    initialText: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var text by remember { mutableStateOf(initialText) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if (initialText.isEmpty()) R.string.summera_add_prompt else R.string.summera_edit_prompt)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(stringResource(R.string.summera_prompt_hint)) },
            )
        },
        confirmButton = {
            Button(onClick = { onSave(text.trim()) }, enabled = text.isNotBlank()) {
                Text(stringResource(R.string.summera_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.summera_cancel)) }
        },
    )
}

// No java.time on API 24 without desugaring
private fun utcNow(): String {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(Date())
}
