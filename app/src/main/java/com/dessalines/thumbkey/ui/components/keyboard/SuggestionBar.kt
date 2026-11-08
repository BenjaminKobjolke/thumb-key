package com.dessalines.thumbkey.ui.components.keyboard

import android.content.Context
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    val words: List<Suggestion?>,
    val abbreviation: Abbreviation?,
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
    val audioManager = remember(context) { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

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
    val abbreviation = data.abbreviation
    val left = if (abbreviation == null) data.words.getOrNull(0) else data.words.getOrNull(1)
    val center = if (abbreviation == null) data.words.getOrNull(1) else null
    val right = data.words.getOrNull(if (abbreviation == null) 2 else 0)
    val selectBest =
        when {
            abbreviation != null -> {
                {
                    tapFeedback(data.view, data.audioManager, options)
                    data.context.ignoreNextCursorMove()
                    applyExpansion(
                        data.context.appInputConnection,
                        expansionResult(abbreviation.expansion, data.prefix.length),
                    )
                    AbbreviationManager.getInstance(data.context).clearBuffer()
                }
            }

            center != null -> {
                suggestionActions(actionContext, center).onSelect
            }

            else -> {
                null
            }
        }

    DisposableEffect(data.context, selectBest) {
        data.context.acceptBestSuggestion = selectBest
        onDispose {
            if (data.context.acceptBestSuggestion === selectBest) {
                data.context.acceptBestSuggestion = null
            }
        }
    }

    SuggestionBarSlots(
        modifier = Modifier.height(options.height).padding(horizontal = 4.dp),
        left = { SuggestionBarWordSlot(actionContext, left, bold = false) },
        center = {
            if (abbreviation != null) {
                AbbreviationSuggestionItem(abbreviation, data.prefix.length) { selectBest?.invoke() }
            } else {
                SuggestionBarWordSlot(actionContext, center, bold = true)
            }
        },
        right = { SuggestionBarWordSlot(actionContext, right, bold = false) },
    )
}

@Composable
private fun SuggestionBarWordSlot(
    context: SuggestionBarActionContext,
    suggestion: Suggestion?,
    bold: Boolean,
) {
    if (suggestion != null) {
        WordSuggestionItem(
            suggestion = suggestion,
            prefix = context.data.prefix,
            bold = bold,
            actions = suggestionActions(context, suggestion),
        )
    }
}

private fun suggestionActions(
    context: SuggestionBarActionContext,
    suggestion: Suggestion,
): WordSuggestionActions {
    val data = context.data
    val options = context.options
    val scope = context.scope
    val first = suggestion.first
    return WordSuggestionActions(
        onSelect = {
            tapFeedback(data.view, data.audioManager, options)
            data.context.ignoreNextCursorMove()
            data.context.appInputConnection.deleteSurroundingText(data.prefix.length, 0)
            data.context.appInputConnection.commitText(suggestion.text(data.prefix) + " ", 1)
            AbbreviationManager.getInstance(data.context).clearBuffer()
            scope.launch(Dispatchers.IO) {
                val now = System.currentTimeMillis()
                data.wordUsageDao.recordUse(first.word, first.wordKey, now)
                if (options.wordCombinationsEnabled && data.previousKey != null) {
                    data.wordPairDao.recordUse(data.previousKey, first.wordKey, now)
                }
                suggestion.next?.let { next ->
                    data.wordUsageDao.recordUse(next.word, next.wordKey, now)
                    if (options.wordCombinationsEnabled) {
                        data.wordPairDao.recordUse(first.wordKey, next.wordKey, now)
                    }
                }
            }
        },
        onEdit = { context.onEdit(first) },
        onIgnore = { options.onIgnoreRequest(first) },
        onDelete = { scope.launch(Dispatchers.IO) { data.wordUsageDao.deleteById(first.id) } },
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
