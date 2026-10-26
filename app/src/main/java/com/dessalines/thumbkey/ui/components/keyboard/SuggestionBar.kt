package com.dessalines.thumbkey.ui.components.keyboard

import android.content.Context
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.Abbreviation
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.db.WordPairDao
import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.db.WordUsageDao
import com.dessalines.thumbkey.utils.AbbreviationManager
import com.dessalines.thumbkey.utils.KeyboardFieldConnection
import com.dessalines.thumbkey.utils.applyExpansion
import com.dessalines.thumbkey.utils.expansionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class SuggestionBarOptions(
    val height: Dp,
    val vibrateOnTap: Boolean,
    val soundOnTap: Boolean,
    val wordCombinationsEnabled: Boolean,
    val onIgnoreRequest: (WordUsage) -> Unit,
)

private data class SuggestionBarData(
    val context: IMEService,
    val view: View,
    val audioManager: AudioManager,
    val wordUsageDao: WordUsageDao,
    val wordPairDao: WordPairDao,
    val prefix: String,
    val previousKey: String?,
    val words: List<WordUsage>,
    val abbreviation: Abbreviation?,
    val listState: LazyListState,
)

private data class SuggestionBarEditor(
    val word: WordUsage?,
    val text: String,
    val field: KeyboardFieldConnection?,
)

private data class SuggestionBarEditorActions(
    val onSave: () -> Unit,
    val onCancel: () -> Unit,
    val onEdit: (WordUsage) -> Unit,
)

private data class SuggestionBarEditing(
    val editor: SuggestionBarEditor,
    val actions: SuggestionBarEditorActions,
)

private data class SuggestionBarActionContext(
    val data: SuggestionBarData,
    val options: SuggestionBarOptions,
    val scope: CoroutineScope,
    val onEdit: (WordUsage) -> Unit,
)

@Composable
fun SuggestionBar(options: SuggestionBarOptions) {
    val ctx = LocalContext.current as IMEService
    val view = LocalView.current
    val data = rememberSuggestionBarData(ctx, view, options)
    val editing = rememberSuggestionBarEditing(ctx, view, data.wordUsageDao)
    SuggestionBarContent(data, options, editing.editor, editing.actions)
}

@Composable
private fun rememberSuggestionBarEditing(
    context: IMEService,
    view: View,
    dao: WordUsageDao,
): SuggestionBarEditing {
    val scope = rememberCoroutineScope()
    var editingWord by remember { mutableStateOf<WordUsage?>(null) }
    var editText by remember(editingWord?.id) { mutableStateOf(editingWord?.word.orEmpty()) }
    val field =
        remember(editingWord?.id) {
            editingWord?.let { KeyboardFieldConnection(view, it.word) { editText = it } }
        }
    DisposableEffect(field) {
        if (field != null) context.setInputRedirect(field)
        onDispose { if (field != null) context.setInputRedirect(null) }
    }
    return SuggestionBarEditing(
        editor = SuggestionBarEditor(editingWord, editText, field),
        actions =
            SuggestionBarEditorActions(
                onSave = {
                    val word = editText.trim()
                    val key = word.lowercase()
                    val id = editingWord!!.id
                    scope.launch(Dispatchers.IO) { dao.rename(id, word, key) }
                    editingWord = null
                },
                onCancel = { editingWord = null },
                onEdit = { editingWord = it },
            ),
    )
}

@Composable
private fun rememberSuggestionBarData(
    context: IMEService,
    view: View,
    options: SuggestionBarOptions,
): SuggestionBarData {
    val database = remember(context) { AppDB.getDatabase(context) }
    val wordUsageDao = remember(database) { database.wordUsageDao() }
    val wordPairDao = remember(database) { database.wordPairDao() }
    val abbreviationDao = remember(database) { database.abbreviationDao() }
    val prefix = context.suggestionContext.prefix
    val previousKey = context.suggestionContext.previousKey
    val lastWordKey = context.suggestionContext.lastWordKey
    val words by remember(prefix, previousKey, options.wordCombinationsEnabled, lastWordKey) {
        observeSuggestedWords(
            SuggestedWordsQuery(
                wordUsageDao,
                wordPairDao,
                prefix,
                previousKey,
                options.wordCombinationsEnabled,
                lastWordKey,
            ),
        )
    }.collectAsState(initial = emptyList())
    val abbreviation by remember(prefix) {
        if (prefix.isEmpty()) flowOf<Abbreviation?>(null) else abbreviationDao.observeAbbreviation(prefix)
    }.collectAsState(initial = null)
    val listState = rememberLazyListState()
    val audioManager = remember(context) { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    LaunchedEffect(prefix) { listState.scrollToItem(0) }
    return SuggestionBarData(
        context,
        view,
        audioManager,
        wordUsageDao,
        wordPairDao,
        prefix,
        previousKey,
        words,
        abbreviation,
        listState,
    )
}

@Composable
private fun SuggestionBarContent(
    data: SuggestionBarData,
    options: SuggestionBarOptions,
    editor: SuggestionBarEditor,
    actions: SuggestionBarEditorActions,
) {
    if (editor.word != null && editor.field != null) {
        SuggestionBarEditField(options.height, editor.text, actions.onSave, actions.onCancel)
    } else {
        SuggestionBarWordList(data, options, actions.onEdit)
    }
}

@Composable
private fun SuggestionBarWordList(
    data: SuggestionBarData,
    options: SuggestionBarOptions,
    onEdit: (WordUsage) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val actionContext = SuggestionBarActionContext(data, options, scope, onEdit)
    LazyRow(
        state = data.listState,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = Modifier.fillMaxWidth().height(options.height),
    ) {
        data.abbreviation?.let { abbreviation ->
            item(key = "abbreviation-${abbreviation.id}") {
                AbbreviationSuggestionItem(abbreviation, data.prefix.length) {
                    tapFeedback(data.view, data.audioManager, options)
                    data.context.ignoreNextCursorMove()
                    applyExpansion(
                        data.context.appInputConnection,
                        expansionResult(abbreviation.expansion, data.prefix.length),
                    )
                    AbbreviationManager.getInstance(data.context).clearBuffer()
                }
            }
        }
        items(data.words, key = { it.id }) { word ->
            WordSuggestionItem(
                word = word,
                actions = suggestionActions(actionContext, word),
            )
        }
    }
}

private fun suggestionActions(
    context: SuggestionBarActionContext,
    word: WordUsage,
): WordSuggestionActions {
    val data = context.data
    val options = context.options
    val scope = context.scope
    return WordSuggestionActions(
        onSelect = {
            tapFeedback(data.view, data.audioManager, options)
            data.context.ignoreNextCursorMove()
            data.context.appInputConnection.deleteSurroundingText(data.prefix.length, 0)
            data.context.appInputConnection.commitText(word.word + " ", 1)
            AbbreviationManager.getInstance(data.context).clearBuffer()
            scope.launch(Dispatchers.IO) {
                val now = System.currentTimeMillis()
                data.wordUsageDao.recordUse(word.word, word.wordKey, now)
                if (options.wordCombinationsEnabled && data.previousKey != null) {
                    data.wordPairDao.recordUse(data.previousKey, word.wordKey, now)
                }
            }
        },
        onEdit = { context.onEdit(word) },
        onIgnore = { options.onIgnoreRequest(word) },
        onDelete = { scope.launch(Dispatchers.IO) { data.wordUsageDao.deleteById(word.id) } },
    )
}

private fun tapFeedback(
    view: android.view.View,
    audioManager: AudioManager,
    options: SuggestionBarOptions,
) {
    if (options.vibrateOnTap) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    if (options.soundOnTap) audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, .1f)
}
