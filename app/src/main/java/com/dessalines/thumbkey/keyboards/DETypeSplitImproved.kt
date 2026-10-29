@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val DE_TYPE_SPLIT_IMPROVED_RETURN_KEY_ITEM =
    RETURN_KEY_ITEM.copy(
        swipeType = FOUR_WAY_CROSS,
        left = KeyC(display = null, action = CommitText("\n")),
        top =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardTab),
                action = CommitText("\t"),
                color = SECONDARY,
            ),
    )

val KB_DE_TYPESPLIT_IMPROVED: KeyboardDefinition =
    KeyboardDefinition(
        title = "deutsch type-split improved",
        modes =
            KeyboardDefinitionModes(
                main = KB_DE_TYPESPLIT_IMPROVED_MAIN,
                shifted = KB_DE_TYPESPLIT_IMPROVED_SHIFTED,
                numeric = KB_DE_TYPESPLIT_IMPROVED_NUMERIC,
            ),
    )
