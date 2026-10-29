@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val SPACEBAR_FRENCH_NEXT_TAP_ACTIONS =
    listOf(
        ReplaceLastText(", ", trimCount = 1),
        ReplaceLastText(". "),
        ReplaceLastText(" ? "),
        ReplaceLastText(" ! ", trimCount = 3),
        ReplaceLastText(" : ", trimCount = 3),
        ReplaceLastText(" ; ", trimCount = 3),
    )
val SPACEBAR_FRENCH_KEY_ITEM =
    SPACEBAR_KEY_ITEM.copy(
        nextTapActions = SPACEBAR_FRENCH_NEXT_TAP_ACTIONS,
    )

val SPACEBAR_FRENCH_SKINNY_KEY_ITEM = SPACEBAR_FRENCH_KEY_ITEM.copy(widthMultiplier = 1)

val SPACEBAR_FRENCH_TYPESPLIT_MIDDLE_KEY_ITEM =
    SPACEBAR_TYPESPLIT_MIDDLE_KEY_ITEM.copy(
        nextTapActions = SPACEBAR_FRENCH_NEXT_TAP_ACTIONS,
    )

val SPACEBAR_FRENCH_TYPESPLIT_BOTTOM_KEY_ITEM =
    SPACEBAR_TYPESPLIT_BOTTOM_KEY_ITEM.copy(
        nextTapActions = SPACEBAR_FRENCH_NEXT_TAP_ACTIONS,
    )

val SPACEBAR_FARSI_KEY_ITEM =
    SPACEBAR_KEY_ITEM.copy(
        nextTapActions =
            listOf(
                ReplaceLastText("، ", trimCount = 1),
                ReplaceLastText(". "),
                ReplaceLastText("؟ "),
                ReplaceLastText("! "),
                ReplaceLastText(": "),
                ReplaceLastText("؛ "),
            ),
    )

val SPACEBAR_FARSI_SKINNY_KEY_ITEM = SPACEBAR_FARSI_KEY_ITEM.copy(widthMultiplier = 1)

val SPACEBAR_ZERO_KEY_ITEM = SPACEBAR_KEY_ITEM.copy(longPress = CommitText("0"))
