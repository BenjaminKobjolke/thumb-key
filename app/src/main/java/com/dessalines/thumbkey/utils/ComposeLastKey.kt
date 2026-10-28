package com.dessalines.thumbkey.utils

/** Resolves a compose modifier and the preceding text to their composed character. */
fun composeLastKey(
    text: String,
    textBefore: CharSequence?,
): CharSequence? {
    val table =
        COMPOSE_ACCENT_TABLES[text]
            ?: COMPOSE_SYMBOL_TABLES[text]
            ?: throw IllegalStateException("Invalid key modifier")

    return (textBefore as? String)?.let { table[it] } ?: textBefore
}
