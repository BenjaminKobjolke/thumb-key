package com.dessalines.thumbkey.ui.components.keyboard

import com.dessalines.thumbkey.utils.ColorVariant
import com.dessalines.thumbkey.utils.FontSizeVariant
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyC
import com.dessalines.thumbkey.utils.KeyDisplay
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.SwipeDirection
import com.dessalines.thumbkey.utils.SwipeNWay
import com.dessalines.thumbkey.utils.isCornerDirection
import com.dessalines.thumbkey.utils.sketchRank
import com.dessalines.thumbkey.utils.sketchSlot
import io.github.toldry.glyphsketch.Charset
import io.github.toldry.glyphsketch.Tile

/** Candidate key `key`: its center, sides and corners type candidates, see [sketchRank]. */
internal fun candidateKeyItem(
    key: Int,
    tiles: List<Tile>,
    charset: Charset?,
    cornerLabels: Boolean,
): KeyItemC {
    fun slot(direction: SwipeDirection?): KeyC {
        val tile = tiles.getOrNull(sketchRank(key, direction))
        // An empty slot does nothing, rather than typing the center
        if (tile == null || charset == null) return KeyC(action = KeyAction.Noop, display = null)
        val info = charset.characters.getValue(tile.representative)
        val corner = isCornerDirection(direction)
        return KeyC(
            action = KeyAction.Sketch.CommitCandidate(info.text, info.displayText),
            swipeReturnAction = KeyAction.Sketch.ShowLookAlikes(tile.members),
            display = if (corner && !cornerLabels) null else KeyDisplay.TextDisplay(info.displayText),
            size =
                when {
                    direction == null -> FontSizeVariant.LARGE
                    corner -> FontSizeVariant.SMALLEST
                    else -> FontSizeVariant.SMALL
                },
            color = if (direction == null) ColorVariant.PRIMARY else ColorVariant.SECONDARY,
        )
    }

    return KeyItemC(
        center = slot(null),
        left = slot(SwipeDirection.LEFT),
        topLeft = slot(SwipeDirection.TOP_LEFT),
        top = slot(SwipeDirection.TOP),
        topRight = slot(SwipeDirection.TOP_RIGHT),
        right = slot(SwipeDirection.RIGHT),
        bottomRight = slot(SwipeDirection.BOTTOM_RIGHT),
        bottom = slot(SwipeDirection.BOTTOM),
        bottomLeft = slot(SwipeDirection.BOTTOM_LEFT),
        swipeType = SwipeNWay.EIGHT_WAY,
        backgroundColor = ColorVariant.SURFACE,
        longPress = tiles.getOrNull(key)?.let { KeyAction.Sketch.ShowLookAlikes(it.members) },
    )
}
