package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.utils.SwipeDirection
import com.dessalines.thumbkey.utils.sketchSlot
import io.github.toldry.glyphsketch.Charset
import io.github.toldry.glyphsketch.Tile

internal class SketchRow(
    val number: Int,
    val codePoint: Int,
    val slot: Pair<Int, SwipeDirection?>?,
)

internal data class SketchListActions(
    val onPick: (text: String) -> Unit,
    val onSearch: () -> Unit,
    val onClose: () -> Unit,
)

internal data class SketchListContent(
    val list: SketchList,
    val tiles: List<Tile>,
    val charset: Charset,
)

/**
 * The rows of `list` that match every word of `query` by name or code point. Searching all
 * candidates also finds members of their look-alike groups.
 */
internal fun sketchListRows(
    list: SketchList,
    query: String,
    tiles: List<Tile>,
    charset: Charset,
): List<SketchRow> {
    val words = query.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

    fun matches(codePoint: Int): Boolean {
        val name = charset.characters.getValue(codePoint).name
        val code = "%04X".format(codePoint)
        return words.all { word ->
            name.contains(word, ignoreCase = true) ||
                code.contains(word.removePrefix("U+").removePrefix("u+"), ignoreCase = true)
        }
    }

    return when (list) {
        SketchList.Candidates -> {
            tiles.flatMapIndexed { rank, tile ->
                val codePoints = if (words.isEmpty()) listOf(tile.representative) else tile.members.filter(::matches)
                // Only the candidate itself is on a key, not the rest of its group
                codePoints.map { SketchRow(rank + 1, it, if (it == tile.representative) sketchSlot(rank) else null) }
            }
        }

        is SketchList.LookAlikes -> {
            val ranks = tiles.withIndex().associate { (rank, tile) -> tile.representative to rank }
            list.codePoints
                .mapIndexed { index, codePoint -> SketchRow(index + 1, codePoint, ranks[codePoint]?.let(::sketchSlot)) }
                .filter { matches(it.codePoint) }
        }
    }
}

/** A list over the whole sketch keyboard: all candidates, or one candidate's look-alikes. */
@Composable
internal fun SketchListScreen(
    content: SketchListContent,
    actions: SketchListActions,
    modifier: Modifier = Modifier,
) {
    val (list, tiles, charset) = content
    val rows = remember(list, tiles, charset) { sketchListRows(list, "", tiles, charset) }
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        SketchListHeader(list, rows.size, charset, actions.onClose)
        SearchField(
            text = "",
            hint = stringResource(R.string.sketch_search_hint),
            modifier = Modifier.clickable(onClick = actions.onSearch),
        )
        LazyColumn {
            items(rows) { CandidateRow(it, charset, actions.onPick) }
        }
    }
}

@Composable
private fun SketchListHeader(
    list: SketchList,
    rowCount: Int,
    charset: Charset,
    onClose: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 4.dp),
    ) {
        // A back button at each end, like the clipboard history, for either thumb
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.sketch_back),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text =
                when (list) {
                    SketchList.Candidates -> {
                        stringResource(R.string.sketch_all_candidates, rowCount)
                    }

                    is SketchList.LookAlikes -> {
                        val first = charset.characters.getValue(list.codePoints.first()).displayText
                        stringResource(R.string.sketch_look_alikes, first, rowCount)
                    }
                },
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.sketch_back),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
