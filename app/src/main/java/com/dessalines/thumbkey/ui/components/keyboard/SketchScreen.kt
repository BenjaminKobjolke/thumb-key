package com.dessalines.thumbkey.ui.components.keyboard

import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.keyboards.BACKSPACE_KEY_ITEM
import com.dessalines.thumbkey.keyboards.RETURN_KEY_ITEM
import com.dessalines.thumbkey.keyboards.SKETCH_BACK_KEY_ITEM
import com.dessalines.thumbkey.keyboards.SKETCH_UNDO_KEY_ITEM
import com.dessalines.thumbkey.keyboards.SPACEBAR_SKINNY_KEY_ITEM
import com.dessalines.thumbkey.utils.ColorVariant
import com.dessalines.thumbkey.utils.FontSizeVariant
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyC
import com.dessalines.thumbkey.utils.KeyDisplay
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.KeyboardFieldConnection
import com.dessalines.thumbkey.utils.SKETCH_CANDIDATE_KEYS
import com.dessalines.thumbkey.utils.SKETCH_LIST_SIZE
import com.dessalines.thumbkey.utils.SketchRecognizer
import com.dessalines.thumbkey.utils.SwipeDirection
import com.dessalines.thumbkey.utils.SwipeNWay
import com.dessalines.thumbkey.utils.TAG
import com.dessalines.thumbkey.utils.isCornerDirection
import com.dessalines.thumbkey.utils.sketchRank
import com.dessalines.thumbkey.utils.sketchSlot
import com.dessalines.thumbkey.utils.swipeDirectionArrow
import io.github.toldry.glyphsketch.Charset
import io.github.toldry.glyphsketch.Point
import io.github.toldry.glyphsketch.Recognizer
import io.github.toldry.glyphsketch.Tile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Corner labels need a big enough key to stay readable. Smaller keys keep the corner swipes.
private const val SKETCH_CORNER_LABELS_MIN_KEY_SIZE = 80f

/**
 * Sketch mode: draw a character on the pad and type it from the candidate keys below.
 * The pad takes three columns, above the row of three candidate keys; the fourth column,
 * on the right or for a left-handed layout on the left, holds the controls. `key` draws a keyboard key that sends sketch actions to
 * `onSketchAction`. `onSearch` opens the open list's search.
 */
@Composable
fun SketchScreen(
    state: SketchState,
    rowCount: Int,
    leftHanded: Boolean,
    keyWidth: Float,
    keyHeight: Float,
    keyPadding: Int,
    keyBorderWidth: Float,
    cornerRadius: Float,
    vibrateOnTap: Boolean,
    soundOnTap: Boolean,
    onCommit: (text: String) -> Unit,
    onSearch: () -> Unit,
    key: @Composable (key: KeyItemC, onSketchAction: (KeyAction.Sketch) -> Unit) -> Unit,
) {
    val ctx = LocalContext.current
    val feedback = rememberSketchFeedback(vibrateOnTap, soundOnTap)

    val recognizer by produceState<Result<Recognizer>?>(null) {
        value = runCatching { SketchRecognizer.get(ctx) }
    }
    val loaded = recognizer?.getOrNull()

    // Recognize after each stroke. A new stroke restarts this, dropping the older query.
    LaunchedEffect(loaded, state.strokes) {
        val strokes = state.strokes
        if (loaded == null || strokes.isEmpty()) {
            state.tiles = emptyList()
            return@LaunchedEffect
        }
        val points = strokes.map { stroke -> stroke.map { Point(it.x.toDouble(), it.y.toDouble()) } }
        val result =
            withContext(Dispatchers.Default) {
                loaded.recognize(
                    points,
                    // Only picks a group's representative, which is set below
                    language = null,
                    tiles = SKETCH_LIST_SIZE,
                    characters = 0,
                    accept = SketchRecognizer.displayable,
                )
            }
        with(result.timings) {
            Log.d(
                TAG,
                "glyphsketch: recognized ${strokes.size} strokes in " +
                    "%.1f ms: rasterize %.1f, encode %.1f, rank %.1f".format(totalMs, rasterizeMs, encodeMs, rankMs),
            )
        }
        // A look-alike group shows as its lowest code point, its members in code point order,
        // whatever the keyboard's language
        state.tiles =
            result.tiles.map { tile ->
                val members = tile.members.sorted()
                tile.copy(representative = members.first(), members = members)
            }
    }

    // The drawing stays on the pad, for another candidate or a look-alike
    fun commit(text: String) {
        onCommit(text)
        state.list = null
    }

    val onSketchAction: (KeyAction.Sketch) -> Unit = { action ->
        when (action) {
            is KeyAction.Sketch.CommitCandidate -> {
                commit(action.text)
            }

            is KeyAction.Sketch.ShowLookAlikes -> {
                state.list = SketchList.LookAlikes(action.codePoints)
            }

            KeyAction.Sketch.UndoStroke -> {
                state.strokes = state.strokes.dropLast(1)
            }

            KeyAction.Sketch.ClearPad -> {
                state.strokes = emptyList()
            }

            KeyAction.Sketch.ShowCandidateList -> {
                // Nothing to list before drawing
                if (state.strokes.isNotEmpty()) state.list = SketchList.Candidates
            }
        }
    }

    val cornerLabels = (keyWidth + keyHeight) / 2 >= SKETCH_CORNER_LABELS_MIN_KEY_SIZE
    // The best candidates sit nearest the thumb: #1 on the right, or on the left for a
    // left-handed layout
    val candidateKeys =
        remember(state.tiles, loaded, cornerLabels, leftHanded) {
            val keys = (0 until SKETCH_CANDIDATE_KEYS).map { candidateKeyItem(it, state.tiles, loaded?.charset, cornerLabels) }
            if (leftHanded) keys else keys.asReversed()
        }
    val controlKeys =
        listOf(SKETCH_BACK_KEY_ITEM, SKETCH_UNDO_KEY_ITEM, BACKSPACE_KEY_ITEM, SPACEBAR_SKINNY_KEY_ITEM, RETURN_KEY_ITEM)
            .take(rowCount.coerceAtLeast(3))

    val padShape = RoundedCornerShape(cornerRadius.dp)
    val controls =
        @Composable {
            Column {
                controlKeys.forEach { key(it, onSketchAction) }
            }
        }
    Box {
        // The pad fills what the candidate keys leave of the control column's height, so both
        // columns end on the same pixel. The controls are on the right, or on the left for a
        // left-handed layout.
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            if (leftHanded) controls()
            Column(modifier = Modifier.fillMaxHeight()) {
                Box(
                    modifier =
                        Modifier
                            .width((keyWidth * SKETCH_CANDIDATE_KEYS).dp)
                            .weight(1f)
                            .padding(keyPadding.dp)
                            .clip(padShape)
                            .then(
                                if (keyBorderWidth > 0.0) {
                                    Modifier.border(keyBorderWidth.dp, MaterialTheme.colorScheme.outline, padShape)
                                } else {
                                    Modifier
                                },
                            ).background(MaterialTheme.colorScheme.surface),
                ) {
                    SketchPad(
                        strokes = state.strokes,
                        hint =
                            when {
                                recognizer == null -> stringResource(R.string.sketch_loading)
                                loaded == null -> stringResource(R.string.sketch_load_failed)
                                else -> stringResource(R.string.sketch_hint)
                            },
                        onStroke = { state.strokes = state.strokes + listOf(it) },
                    )
                }
                Row {
                    candidateKeys.forEach { key(it, onSketchAction) }
                }
            }
            if (!leftHanded) controls()
        }
        val charset = loaded?.charset
        val list = state.list
        if (list != null && charset != null) {
            SketchListScreen(
                content = SketchListContent(list, state.tiles, charset),
                actions =
                    SketchListActions(
                        onPick = {
                            feedback()
                            commit(it)
                        },
                        onSearch = onSearch,
                        onClose = { state.list = null },
                    ),
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}

@Composable
internal fun rememberSketchFeedback(
    vibrateOnTap: Boolean,
    soundOnTap: Boolean,
): () -> Unit {
    val ctx = LocalContext.current
    val view = LocalView.current
    val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    return {
        if (vibrateOnTap) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        if (soundOnTap) audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, .1f)
    }
}
