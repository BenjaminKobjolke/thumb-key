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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier =
                    Modifier
                        .padding(padding)
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
                    error?.let {
                        Preference(title = { Text(stringResource(R.string.summera_failed, it)) })
                    }
                    if (credentials == null) {
                        Preference(title = { Text(stringResource(R.string.summera_sign_in_first)) })
                        return@ProvidePreferenceTheme
                    }
                    val list = replacements
                    if (list == null) {
                        if (error == null) {
                            Preference(
                                title = { Text(stringResource(R.string.summera_loading)) },
                                icon = { CircularProgressIndicator() },
                            )
                        }
                        return@ProvidePreferenceTheme
                    }

                    if (!transcript.isNullOrBlank()) {
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
                                        selected = from == word,
                                        onClick = { from = word },
                                        label = { Text(word) },
                                    )
                                }
                        }
                    }

                    OutlinedTextField(
                        value = from,
                        onValueChange = { from = it },
                        label = { Text(stringResource(R.string.summera_replacement_from)) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                    )
                    OutlinedTextField(
                        value = to,
                        onValueChange = { to = it },
                        label = { Text(stringResource(R.string.summera_replacement_to)) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                    )
                    Button(
                        enabled = from.isNotBlank() && to.isNotBlank(),
                        onClick = {
                            val saved = TranscriptionReplacement(from.trim(), to.trim())
                            save(list.filterNot { it.from.equals(saved.from, ignoreCase = true) } + saved) {
                                from = ""
                                to = ""
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Text(stringResource(R.string.summera_save))
                    }

                    list.forEach { replacement ->
                        Preference(
                            title = { Text("${replacement.from} → ${replacement.to}") },
                            widgetContainer = {
                                IconButton(onClick = { save(list - replacement) }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = stringResource(R.string.summera_delete_replacement),
                                    )
                                }
                            },
                            onClick = {
                                from = replacement.from
                                to = replacement.to
                            },
                        )
                    }
                }
            }
        },
    )
}
