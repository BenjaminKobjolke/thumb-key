package com.dessalines.thumbkey.ui.components.keyboard

import android.content.Context
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.utils.AbbreviationManager
import com.dessalines.thumbkey.utils.KeyboardFieldConnection
import com.dessalines.thumbkey.utils.SUGGESTION_LIMIT
import com.dessalines.thumbkey.utils.escapeLike
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuggestionBar(
    height: Dp,
    vibrateOnTap: Boolean,
    soundOnTap: Boolean,
) {
    val ctx = LocalContext.current as IMEService
    val view = LocalView.current
    val dao = remember { AppDB.getDatabase(ctx).wordUsageDao() }
    val prefix = ctx.suggestionPrefix
    val words by remember(prefix) {
        if (prefix.isEmpty()) dao.recent(SUGGESTION_LIMIT) else dao.suggest(escapeLike(prefix.lowercase()), SUGGESTION_LIMIT)
    }.collectAsState(initial = emptyList())
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val audioManager = remember(ctx) { ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var editingWord by remember { mutableStateOf<WordUsage?>(null) }
    var editText by remember(editingWord?.id) { mutableStateOf(editingWord?.word.orEmpty()) }
    val field =
        remember(editingWord?.id) {
            editingWord?.let { KeyboardFieldConnection(view, it.word) { editText = it } }
        }
    DisposableEffect(field) {
        if (field != null) ctx.setInputRedirect(field)
        onDispose { if (field != null) ctx.setInputRedirect(null) }
    }

    fun tapFeedback() {
        if (vibrateOnTap) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        if (soundOnTap) audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, .1f)
    }

    LaunchedEffect(prefix) { listState.scrollToItem(0) }

    if (editingWord != null && field != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().height(height),
        ) {
            SearchField(
                text = editText,
                hint = stringResource(R.string.word_suggestion_edit_hint),
                cursor = true,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = {
                val word = editText.trim()
                val key = word.lowercase()
                val id = editingWord!!.id
                scope.launch(Dispatchers.IO) { dao.rename(id, word, key) }
                editingWord = null
            }) {
                Icon(Icons.Outlined.Check, contentDescription = stringResource(R.string.save))
            }
            IconButton(onClick = { editingWord = null }) {
                Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.cancel))
            }
        }
    } else {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 4.dp),
            modifier = Modifier.fillMaxWidth().height(height),
        ) {
            items(words, key = { it.id }) { word ->
                var menuExpanded by remember(word.id) { mutableStateOf(false) }
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier =
                            Modifier
                                .combinedClickable(
                                    onClick = {
                                        tapFeedback()
                                        ctx.ignoreNextCursorMove()
                                        ctx.appInputConnection.deleteSurroundingText(prefix.length, 0)
                                        ctx.appInputConnection.commitText(word.word + " ", 1)
                                        AbbreviationManager.getInstance(ctx).clearBuffer()
                                        scope.launch(Dispatchers.IO) {
                                            dao.recordUse(word.word, word.wordKey, System.currentTimeMillis())
                                        }
                                    },
                                    onLongClick = { menuExpanded = true },
                                ).padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(word.word, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.edit_word_suggestion)) },
                            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                editingWord = word
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete_word_suggestion)) },
                            leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                scope.launch(Dispatchers.IO) { dao.deleteById(word.id) }
                            },
                        )
                    }
                }
            }
        }
    }
}
