@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import com.dessalines.thumbkey.utils.KeyboardDefinition
import com.dessalines.thumbkey.utils.KeyboardDefinitionModes

val KB_DE_TYPESPLITBEN: KeyboardDefinition =
    KeyboardDefinition(
        title = "deutsch type-split-ben",
        modes =
            KeyboardDefinitionModes(
                main = KB_DE_TYPESPLITBEN_MAIN,
                shifted = KB_DE_TYPESPLITBEN_SHIFTED,
                numeric = KB_DE_TYPESPLITBEN_NUMERIC,
            ),
    )
