package com.dessalines.thumbkey.ui.components.settings.summera

import android.text.format.DateUtils
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.summera.SummeraAccount
import com.dessalines.thumbkey.utils.SimpleTopAppBar
import de.xida.aichatapi.TranscriptionStatus
import de.xida.aichatapi.TranscriptionSummary
import de.xida.aichatapi.XidaAiException
import de.xida.aichatapi.createdAtMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

private const val PAGE_SIZE = 20
private val PADDING = 16.dp

/** Past transcriptions from services/list, newest first, with a detail view in the same screen. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummeraHistoryScreen(navController: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val items = remember { mutableStateListOf<TranscriptionSummary>() }
    var total by remember { mutableIntStateOf(Int.MAX_VALUE) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selected by remember { mutableStateOf<TranscriptionSummary?>(null) }

    fun loadMore() {
        val credentials = SummeraAccount.credentials(ctx) ?: return
        if (loading) return
        loading = true
        error = null
        scope.launch {
            try {
                val page = withContext(Dispatchers.IO) { SummeraAccount.client().services.list(credentials, items.size, PAGE_SIZE) }
                items.addAll(page.items)
                total = page.total
            } catch (e: XidaAiException) {
                error = e.message.orEmpty()
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadMore() }

    val current = selected
    BackHandler(enabled = current != null) { selected = null }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.summera_history),
                navController = navController,
            )
        },
        content = { padding ->
            if (current != null) {
                DetailView(item = current, modifier = Modifier.padding(padding))
                return@Scaffold
            }
            LazyColumn(
                modifier =
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface),
            ) {
                items(items, key = { it.id }) { item ->
                    ProvidePreferenceTheme {
                        Preference(
                            title = { Text(formatTimestamp(item.createdAtMillis())) },
                            summary = {
                                Text(
                                    text = item.promptResult ?: item.text ?: item.status,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            onClick = { selected = item },
                        )
                    }
                }
                item {
                    ProvidePreferenceTheme {
                        val message = error
                        when {
                            message != null -> {
                                Preference(
                                    title = { Text(stringResource(R.string.summera_failed, message)) },
                                    onClick = { loadMore() },
                                )
                            }

                            loading -> {
                                Preference(
                                    title = { Text(stringResource(R.string.summera_loading)) },
                                    icon = { CircularProgressIndicator() },
                                )
                            }

                            items.size < total -> {
                                Preference(
                                    title = { Text(stringResource(R.string.summera_load_more)) },
                                    onClick = { loadMore() },
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun DetailView(
    item: TranscriptionSummary,
    modifier: Modifier = Modifier,
) {
    val finished = item.status == TranscriptionStatus.COMPLETE || item.status == TranscriptionStatus.ERROR
    SelectionContainer(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(PADDING / 2),
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(PADDING),
        ) {
            Text(
                text = listOfNotNull(formatTimestamp(item.createdAtMillis()), item.language, item.status).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
            )
            item.prompt?.let {
                Label(R.string.summera_prompt)
                Text(it)
                Label(R.string.summera_answer)
                Text(
                    when {
                        item.promptStatus == TranscriptionStatus.ERROR -> stringResource(R.string.summera_prompt_failed)
                        else -> item.promptResult ?: item.promptStatus.orEmpty()
                    },
                )
            }
            Label(R.string.summera_transcript)
            Text(if (finished) item.text.orEmpty() else item.status)
        }
    }
}

@Composable
private fun Label(resId: Int) {
    Text(
        text = stringResource(resId),
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(top = PADDING / 2),
    )
}

private fun formatTimestamp(timestamp: Long): String =
    DateUtils
        .getRelativeTimeSpanString(
            timestamp,
            System.currentTimeMillis(),
            DateUtils.SECOND_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE,
        ).toString()
