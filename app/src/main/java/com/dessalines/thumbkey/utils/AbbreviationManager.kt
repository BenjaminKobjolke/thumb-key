package com.dessalines.thumbkey.utils

import android.content.Context
import android.util.Log
import com.dessalines.thumbkey.db.AbbreviationDao
import com.dessalines.thumbkey.db.AppDB

private const val ABBR_TAG = "AbbreviationManager"
private const val CURSOR_PLACEHOLDER = "$0"

/**
 * Result of checking for an abbreviation expansion.
 *
 * @param shouldExpand Whether an abbreviation was found and should be expanded
 * @param expandedText The text to replace the abbreviation with
 * @param cursorOffset Position to place cursor after expansion (null = end of text)
 * @param abbreviationLength Length of the abbreviation to delete
 */
data class ExpansionResult(
    val shouldExpand: Boolean,
    val expandedText: String,
    val cursorOffset: Int? = null,
    val abbreviationLength: Int = 0,
)

/**
 * Singleton manager for abbreviation expansion with typed character buffer tracking.
 *
 * The buffer tracks characters typed consecutively via this keyboard.
 * This ensures abbreviations only expand when typed directly, not when
 * the user moves the cursor and inserts a space in existing text.
 */
class AbbreviationManager private constructor(
    context: Context,
) {
    init {
        Log.d(ABBR_TAG, "Initializing AbbreviationManager singleton")
    }

    private val abbreviationDao: AbbreviationDao =
        AppDB.getDatabase(context).abbreviationDao()

    // Buffer for tracking consecutively typed characters
    private val typedBuffer = StringBuilder()

    /**
     * Called when a character is typed via the keyboard.
     */
    fun onCharacterTyped(char: String) {
        typedBuffer.append(char)
        Log.d(ABBR_TAG, "onCharacterTyped: '$char', buffer now: '$typedBuffer'")
    }

    /**
     * Called when backspace is pressed.
     */
    fun onBackspace() {
        if (typedBuffer.isNotEmpty()) {
            typedBuffer.deleteCharAt(typedBuffer.length - 1)
            Log.d(ABBR_TAG, "onBackspace: buffer now: '$typedBuffer'")
        }
    }

    /**
     * Clears the typed buffer. Called when cursor moves externally.
     */
    fun clearBuffer() {
        typedBuffer.clear()
        Log.d(ABBR_TAG, "clearBuffer: buffer cleared")
    }

    /**
     * Returns the current buffer content (for cursor movement detection).
     */
    fun getBuffer(): String = typedBuffer.toString()

    /**
     * Checks if the last word in the typed buffer is an abbreviation.
     * If found, returns expansion info including cursor position if $0 placeholder exists.
     *
     * @return ExpansionResult with expansion details
     */
    @Synchronized
    fun checkAndExpand(): ExpansionResult {
        val bufferStr = typedBuffer.toString()
        Log.d(ABBR_TAG, "checkAndExpand: buffer = '$bufferStr'")
        return checkAndExpandText(bufferStr)
    }

    /**
     * Checks if the last word in the given text is an abbreviation.
     * This method is used for direct input connection approach (when buffer tracking is disabled).
     *
     * @param text The text to check (typically from getTextBeforeCursor)
     * @return ExpansionResult with expansion details
     */
    @Synchronized
    fun checkAndExpandText(text: String): ExpansionResult {
        Log.d(ABBR_TAG, "checkAndExpandText: text = '$text'")

        if (text.isEmpty()) {
            Log.d(ABBR_TAG, "checkAndExpandText: text is empty")
            return ExpansionResult(false, "", null, 0)
        }

        // Get the last word from the text
        val words = text.split(Regex("[ \n]"))
        val lastWord =
            words.lastOrNull()?.takeIf { it.isNotEmpty() }
                ?: run {
                    Log.d(ABBR_TAG, "checkAndExpandText: no last word found")
                    return ExpansionResult(false, "", null, 0)
                }

        Log.d(ABBR_TAG, "checkAndExpandText: lastWord = '$lastWord'")

        // Check if the last word is an abbreviation (case-insensitive)
        val abbreviation = abbreviationDao.getAbbreviation(lastWord.lowercase())

        if (abbreviation == null) {
            Log.d(ABBR_TAG, "checkAndExpandText: no abbreviation found")
            return ExpansionResult(false, "", null, 0)
        }

        val expansion = abbreviation.expansion
        Log.d(ABBR_TAG, "checkAndExpandText: found expansion = '$expansion'")

        // Check for cursor placeholder
        return if (expansion.contains(CURSOR_PLACEHOLDER)) {
            val cursorOffset = expansion.indexOf(CURSOR_PLACEHOLDER)
            val cleanExpansion = expansion.replace(CURSOR_PLACEHOLDER, "")
            Log.d(ABBR_TAG, "checkAndExpandText: cursor placeholder at $cursorOffset, clean expansion = '$cleanExpansion'")
            ExpansionResult(true, cleanExpansion, cursorOffset, lastWord.length)
        } else {
            ExpansionResult(true, expansion, null, lastWord.length)
        }
    }

    companion object {
        @Volatile
        private var instance: AbbreviationManager? = null

        fun getInstance(context: Context): AbbreviationManager =
            instance ?: synchronized(this) {
                instance ?: AbbreviationManager(context.applicationContext).also {
                    instance = it
                }
            }
    }
}
