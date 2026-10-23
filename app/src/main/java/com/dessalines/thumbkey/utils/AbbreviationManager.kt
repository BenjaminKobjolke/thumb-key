package com.dessalines.thumbkey.utils

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputConnection
import com.dessalines.thumbkey.db.AbbreviationDao
import com.dessalines.thumbkey.db.AppDB

private const val ABBR_TAG = "AbbreviationManager"
private const val CURSOR_PLACEHOLDER = "$0"
private const val MAX_BUFFER_LENGTH = 100

internal fun lastWord(text: String): String? = text.split(Regex("[ \n]")).lastOrNull()?.takeIf { it.isNotEmpty() }

internal fun typedLastWord(
    buffer: String,
    textBeforeCursor: String,
): String? = lastWord(textBeforeCursor)?.takeIf { buffer.endsWith(it) }

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

internal fun expansionResult(
    expansion: String,
    abbreviationLength: Int,
): ExpansionResult {
    val cursorOffset = expansion.indexOf(CURSOR_PLACEHOLDER).takeIf { it >= 0 }
    val expandedText = expansion.replace(CURSOR_PLACEHOLDER, "")
    return ExpansionResult(true, expandedText, cursorOffset, abbreviationLength)
}

// Keep space-triggered and tapped abbreviation replacements behavior aligned.
internal fun applyExpansion(
    ic: InputConnection,
    result: ExpansionResult,
) {
    ic.deleteSurroundingText(result.abbreviationLength, 0)
    ic.commitText(result.expandedText, 1)
    if (result.cursorOffset != null) {
        val moveBack = result.expandedText.length - result.cursorOffset
        ic.commitText("", -moveBack)
    } else {
        ic.commitText(" ", 1)
    }
}

/**
 * Singleton manager for abbreviation expansion with typed character buffer tracking.
 *
 * The buffer proves that the word immediately before the cursor was typed
 * consecutively via this keyboard. The real text before the cursor determines
 * the word to expand and how many characters to delete.
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
        if (typedBuffer.length > MAX_BUFFER_LENGTH) {
            // ponytail: A single word longer than 100 characters can no longer be matched; raise the cap if that matters.
            typedBuffer.delete(0, typedBuffer.length - MAX_BUFFER_LENGTH)
        }
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
     * Clears the typed buffer. Called after expansion or when a new text field starts.
     */
    fun clearBuffer() {
        typedBuffer.clear()
        Log.d(ABBR_TAG, "clearBuffer: buffer cleared")
    }

    /**
     * Checks if the real last word before the cursor was typed consecutively via this keyboard.
     * If found, returns expansion info including cursor position if $0 placeholder exists.
     *
     * @param textBeforeCursor Real text before the cursor
     * @return ExpansionResult with expansion details
     */
    @Synchronized
    fun typedWordBeforeCursor(textBeforeCursor: String): String? = typedLastWord(typedBuffer.toString(), textBeforeCursor)

    @Synchronized
    fun checkAndExpand(textBeforeCursor: String): ExpansionResult {
        val bufferStr = typedBuffer.toString()
        val word = typedLastWord(bufferStr, textBeforeCursor)
        if (word == null) {
            Log.d(ABBR_TAG, "checkAndExpand: no consecutively typed word before cursor")
            return ExpansionResult(false, "", null, 0)
        }
        Log.d(ABBR_TAG, "checkAndExpand: typed last word = '$word'")
        return checkAndExpandText(word)
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

        val lastWord =
            lastWord(text)
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
        val result = expansionResult(expansion, lastWord.length)
        if (result.cursorOffset != null) {
            Log.d(ABBR_TAG, "checkAndExpandText: cursor placeholder at ${result.cursorOffset}, clean expansion = '${result.expandedText}'")
        }
        return result
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
