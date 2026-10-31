package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.utils.KeyboardFieldConnection
import com.dessalines.thumbkey.utils.SketchRecognizer
import io.github.toldry.glyphsketch.Charset

/**
 * The open list's search, over the keyboard while its letter keys type the query. It finds
 * characters by name or code point.
 */
@Composable
fun SketchSearch(
    state: SketchState,
    height: Dp,
    vibrateOnTap: Boolean,
    soundOnTap: Boolean,
    onPick: (text: String) -> Unit,
    onClose: () -> Unit,
) {
    val ctx = LocalContext.current
    val ime = ctx as IMEService
    val view = LocalView.current
    val feedback = rememberSketchFeedback(vibrateOnTap, soundOnTap)
    val charset = produceState<Charset?>(null) { value = runCatching { SketchRecognizer.get(ctx).charset }.getOrNull() }.value

    // The keys type into the search field while it's shown
    val field = remember { KeyboardFieldConnection(view, state.query) { state.query = it } }
    DisposableEffect(field) {
        ime.setInputRedirect(field)
        onDispose { ime.setInputRedirect(null) }
    }

    val list = state.list ?: SketchList.Candidates
    val rows =
        remember(list, state.query, state.tiles, charset) {
            charset?.let { sketchListRows(list, state.query, state.tiles, it) }.orEmpty()
        }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 4.dp),
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.sketch_back),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            SearchField(
                text = state.query,
                hint = stringResource(R.string.sketch_search_hint),
                cursor = true,
                modifier = Modifier.weight(1f),
            )
            if (state.query.isNotEmpty()) {
                IconButton(onClick = { field.clear() }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.sketch_clear_search),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        LazyColumn(modifier = Modifier.height(height)) {
            if (charset != null && rows.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.sketch_no_matches),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
            if (charset != null) {
                items(rows) { row ->
                    CandidateRow(row, charset) {
                        feedback()
                        onPick(it)
                    }
                }
            }
        }
    }
}
