package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import io.github.toldry.glyphsketch.Tile

/** A list over the whole sketch keyboard, instead of the pad and keys. */
sealed interface SketchList {
    // Every candidate, by rank
    data object Candidates : SketchList

    // One candidate's look-alike group, the candidate first
    class LookAlikes(
        val codePoints: List<Int>,
    ) : SketchList
}

/**
 * What sketch mode shows. IMEService keeps it, so the drawing stays while a list's search
 * borrows the letter keys, when sketch mode is left and opened again, and when the keyboard
 * is closed and opened again.
 */
@Stable
class SketchState {
    var strokes by mutableStateOf(listOf<List<Offset>>())
    var tiles by mutableStateOf(listOf<Tile>())

    // Searching the open list, typed with the layout's own keys
    var searching by mutableStateOf(false)
    var query by mutableStateOf("")

    private var openList by mutableStateOf<SketchList?>(null)

    // The open list, or null for the pad. Closing it clears its search.
    var list: SketchList?
        get() = openList
        set(value) {
            openList = value
            if (value == null) query = ""
        }
}
