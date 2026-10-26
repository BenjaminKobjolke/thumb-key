package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.Abbreviation
import com.dessalines.thumbkey.db.WordPairDao
import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.db.WordUsageDao
import com.dessalines.thumbkey.utils.SUGGESTION_LIMIT
import com.dessalines.thumbkey.utils.escapeLike
import com.dessalines.thumbkey.utils.expansionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

internal data class SuggestedWordsQuery(
    val dao: WordUsageDao,
    val wordPairDao: WordPairDao,
    val prefix: String,
    val previousKey: String?,
    val combinationsEnabled: Boolean,
)

internal fun observeSuggestedWords(query: SuggestedWordsQuery): Flow<List<WordUsage>> {
    val (dao, wordPairDao, prefix, previousKey, combinationsEnabled) = query
    val normalWords =
        if (prefix.isEmpty()) {
            dao.recent(SUGGESTION_LIMIT)
        } else {
            dao.suggest(escapeLike(prefix.lowercase()), SUGGESTION_LIMIT)
        }
    val pairWords =
        if (combinationsEnabled && previousKey != null) {
            wordPairDao.nextWords(previousKey, escapeLike(prefix.lowercase()), SUGGESTION_LIMIT)
        } else {
            flowOf(emptyList<WordUsage>())
        }

    return combine(pairWords, normalWords) { pairs, normal ->
        (pairs + normal).distinctBy { it.id }.take(SUGGESTION_LIMIT)
    }
}

@Composable
internal fun SuggestionBarEditField(
    height: Dp,
    text: String,
    onSave: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(height),
    ) {
        SearchField(
            text = text,
            hint = stringResource(R.string.word_suggestion_edit_hint),
            cursor = true,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onSave) {
            Icon(Icons.Outlined.Check, contentDescription = stringResource(R.string.save))
        }
        IconButton(onClick = onCancel) {
            Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.cancel))
        }
    }
}

@Composable
internal fun AbbreviationSuggestionItem(
    abbreviation: Abbreviation,
    prefixLength: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = expansionResult(abbreviation.expansion, prefixLength).expandedText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 200.dp),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun WordSuggestionItem(
    word: WordUsage,
    actions: WordSuggestionActions,
) {
    var menuExpanded by remember(word.id) { mutableStateOf(false) }
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier
                    .combinedClickable(
                        onClick = actions.onSelect,
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
                    actions.onEdit()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.word_suggestion_ignore)) },
                leadingIcon = { Icon(Icons.Outlined.Block, contentDescription = null) },
                onClick = {
                    menuExpanded = false
                    actions.onIgnore()
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete_word_suggestion)) },
                leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
                onClick = {
                    menuExpanded = false
                    actions.onDelete()
                },
            )
        }
    }
}

internal data class WordSuggestionActions(
    val onSelect: () -> Unit,
    val onEdit: () -> Unit,
    val onIgnore: () -> Unit,
    val onDelete: () -> Unit,
)
