# Abbreviations Feature

This document describes the abbreviations feature, which is a custom addition to this fork of Thumb-Key. This feature is **not part of the original [dessalines/thumb-key](https://github.com/dessalines/thumb-key) repository**.

## Overview

The abbreviations feature allows users to define short text snippets (abbreviations) that automatically expand into longer text when followed by a space. For example, typing `brb` followed by a space could automatically expand to `be right back `.

The abbreviation character buffer is now always fed as the user types so word suggestions can learn consecutively typed words even when buffer-based abbreviation matching is disabled. See [Word suggestions](WORD_SUGGESTIONS.md) for details.

### Key Features

- **Consecutive typing detection**: Abbreviations only expand when typed directly via the keyboard. Moving the cursor and inserting a space won't trigger expansion.
- **Cursor positioning**: Use `$0` in the expansion to specify where the cursor should be placed after expansion.

## How It Works

### User Flow

1. User navigates to **Settings > Abbreviations**
2. User adds abbreviation entries (e.g., `brb` -> `be right back`)
3. While typing, when the user types an abbreviation followed by a space:
   - The keyboard detects the abbreviation
   - Deletes the typed abbreviation
   - Inserts the expanded text followed by a space

### Technical Flow

```
User types "brb "
    -> Each character typed is added to AbbreviationManager's internal buffer
    -> KeyAction.CommitText(" ") triggered
    -> Utils.kt: performKeyAction() intercepts space key
    -> AbbreviationManager.checkAndExpand(textBeforeCursor) checks the real last word against the typed buffer
    -> If match found:
       - Delete abbreviation from text
       - Insert expansion
       - If expansion contains $0: position cursor there
       - Else: add trailing space
       - Clear buffer
    -> If no match: insert space normally, add to buffer
```

### Consecutive Typing Detection

The **Use buffer tracking** switch selects how the keyboard checks for an abbreviation:

- **On (default)**: the keyboard reads the real text before the cursor and checks whether its last word is also the end of the in-memory buffer of characters typed consecutively with this keyboard. Only then can it expand. It deletes the length of that real word, so a stale buffer cannot make it delete unrelated text.
- **Off**: the keyboard reads the real text before the cursor and expands its last word if it matches a saved abbreviation, whether or not this keyboard typed it.

For example, with tracking on, typing `hl ` directly can expand to `hello `, while moving the cursor into `hl@mail.com` and pressing space does not expand. A delete gesture, cut, paste, or other untracked edit can make the buffer diverge from the text; the real-text check then prevents an incorrect expansion. Text after an untracked separator such as Enter can still expand when the real last word matches the buffer's tail.

The buffer is cleared after a successful expansion and whenever a new text field starts. Backspace removes one character from it. Other deletion operations are not tracked. The buffer keeps at most the last 100 characters, so a single word longer than 100 characters cannot match in tracking mode.

### Cursor Positioning with `$0`

Use the `$0` placeholder in your expansion to control cursor position:

| Abbreviation | Expansion | Result after typing `ff ` |
|--------------|-----------|---------------------------|
| `ff` | `file:$0.md` | `file:│.md` (cursor at │) |
| `hl` | `hello` | `hello │` (cursor at end) |

## Architecture

### Files Overview

| File | Purpose |
|------|---------|
| `db/AppDb.kt` | Database entity (`Abbreviation`) and DAO (`AbbreviationDao`) definitions |
| `db/AbbreviationRepository.kt` | Repository pattern + ViewModel for abbreviations |
| `utils/AbbreviationManager.kt` | Core expansion logic - checks and expands abbreviations |
| `ui/screens/AbbreviationsScreen.kt` | Settings UI for managing abbreviations |
| `ui/components/settings/SettingsScreen.kt` | Navigation entry point to abbreviations |
| `MainActivity.kt` | Navigation route registration |
| `utils/Utils.kt` | Integration point in `performKeyAction()` |
| `res/values/strings.xml` | String resources for UI |

### Database Schema

```kotlin
@Entity
data class Abbreviation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "abbreviation") val abbreviation: String,
    @ColumnInfo(name = "expansion") val expansion: String,
)
```

The `Abbreviation` table is created automatically by Room as part of the database schema (version 21+). No explicit migration is needed for the table itself since Room handles entity creation.

### Key Components

#### 1. AbbreviationManager (`utils/AbbreviationManager.kt`)

A singleton class that handles abbreviation detection, expansion, and typed character tracking:

```kotlin
class AbbreviationManager private constructor(context: Context) {
    fun onCharacterTyped(char: String)  // Track typed characters
    fun onBackspace()                    // Remove last character from buffer
    fun clearBuffer()                    // Clear the typed buffer
    fun checkAndExpand(textBeforeCursor: String): ExpansionResult // Check real last word against typed buffer

    companion object {
        fun getInstance(context: Context): AbbreviationManager
    }
}

data class ExpansionResult(
    val shouldExpand: Boolean,
    val expandedText: String,
    val cursorOffset: Int? = null,      // Position for $0 placeholder
    val abbreviationLength: Int = 0
)
```

- **Buffer**: Tracks up to 100 recent characters typed via this keyboard; it is evidence of consecutive typing
- **Matching**: Reads the real last word before the cursor, then checks whether it ends the buffer; abbreviation lookup is case-insensitive
- **Cursor Offset**: Returns position of `$0` placeholder if present

#### 2. AbbreviationDao (`db/AppDb.kt`)

Database access object with the following operations:

```kotlin
interface AbbreviationDao {
    fun getAllAbbreviations(): LiveData<List<Abbreviation>>
    fun getAbbreviation(abbr: String): Abbreviation?
    suspend fun insert(abbr: String, expansion: String)
    suspend fun update(id: Int, newAbbr: String, expansion: String)
    suspend fun deleteById(id: Int)
}
```

#### 3. Integration Point (`utils/Utils.kt`)

The abbreviation expansion is triggered in `performKeyAction()` when a space is committed:

```kotlin
is KeyAction.CommitText -> {
    if (text == " ") {
        val currentText = ime.currentInputConnection.getTextBeforeCursor(1000, 0)
        val abbreviationManager = AbbreviationManager(ime.applicationContext)
        val (shouldExpand, expandedText) = abbreviationManager.checkAndExpand(currentText)

        if (shouldExpand) {
            // Delete abbreviation and insert expansion
            val lastWord = currentText.split(Regex("[ \n]")).last()
            ime.currentInputConnection.deleteSurroundingText(lastWord.length, 0)
            ime.currentInputConnection.commitText(expandedText + " ", 1)
        }
    }
}
```

## Extending the Feature

### Adding New Trigger Characters

Currently, abbreviations only expand when followed by a space. To add additional trigger characters (e.g., period, comma):

1. Modify `Utils.kt` in `performKeyAction()`:

```kotlin
is KeyAction.CommitText -> {
    val text = action.text
    val triggerChars = listOf(" ", ".", ",", "!", "?")

    if (text in triggerChars) {
        // ... abbreviation expansion logic
    }
}
```

### Adding Import/Export Functionality

To allow users to backup and restore their abbreviations:

1. Add methods to `AbbreviationRepository.kt`:

```kotlin
suspend fun exportToJson(): String {
    val abbreviations = abbreviationDao.getAllAbbreviationsSync()
    return Json.encodeToString(abbreviations)
}

suspend fun importFromJson(json: String) {
    val abbreviations = Json.decodeFromString<List<Abbreviation>>(json)
    abbreviations.forEach { abbreviationDao.insert(it.abbreviation, it.expansion) }
}
```

2. Add UI buttons in `AbbreviationsScreen.kt`
3. Handle file picker/saver intents

### Adding Case Preservation

To preserve the case of the typed abbreviation in the expansion:

1. Modify `AbbreviationManager.checkAndExpand(textBeforeCursor: String)`:

```kotlin
fun checkAndExpand(text: String): Pair<Boolean, String> {
    val lastWord = text.split(Regex("[ \n]")).last()
    val abbreviation = abbreviationDao.getAbbreviation(lastWord.lowercase())

    if (abbreviation != null) {
        val expansion = when {
            lastWord.all { it.isUpperCase() } -> abbreviation.expansion.uppercase()
            lastWord.first().isUpperCase() -> abbreviation.expansion.replaceFirstChar { it.uppercase() }
            else -> abbreviation.expansion
        }
        return Pair(true, expansion)
    }
    return Pair(false, text)
}
```

### Adding Abbreviation Categories/Tags

To organize abbreviations into categories:

1. Modify the database entity:

```kotlin
@Entity
data class Abbreviation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "abbreviation") val abbreviation: String,
    @ColumnInfo(name = "expansion") val expansion: String,
    @ColumnInfo(name = "category") val category: String = "default",
)
```

2. Add a migration in `db/Migrations.kt`:

```kotlin
val MIGRATION_21_22 = object : Migration(21, 22) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Abbreviation ADD COLUMN category TEXT NOT NULL DEFAULT 'default'")
    }
}
```

3. Update the database version in `AppDb.kt`
4. Update UI to show/filter by category

### Adding Regex-Based Abbreviations

For more powerful pattern matching:

1. Add a new field to the entity:

```kotlin
@Entity
data class Abbreviation(
    // ... existing fields
    @ColumnInfo(name = "is_regex") val isRegex: Boolean = false,
)
```

2. Modify `AbbreviationManager`:

```kotlin
fun checkAndExpand(text: String): Pair<Boolean, String> {
    val lastWord = text.split(Regex("[ \n]")).last()

    // Check exact matches first
    val exactMatch = abbreviationDao.getAbbreviation(lastWord.lowercase())
    if (exactMatch != null) {
        return Pair(true, exactMatch.expansion)
    }

    // Check regex patterns
    val regexAbbreviations = abbreviationDao.getRegexAbbreviations()
    for (abbr in regexAbbreviations) {
        if (Regex(abbr.abbreviation).matches(lastWord)) {
            val expansion = lastWord.replace(Regex(abbr.abbreviation), abbr.expansion)
            return Pair(true, expansion)
        }
    }

    return Pair(false, text)
}
```

## Testing

### Manual Testing

1. Add an abbreviation (e.g., `test` -> `testing expansion`)
2. Open any text input field
3. Type `test ` (with trailing space)
4. Verify the text changes to `testing expansion `

### Edge Cases to Test

- Empty abbreviation list
- Case sensitivity (`TEST` vs `test` vs `Test`)
- Abbreviations containing special characters
- Very long expansions
- Rapid typing
- Abbreviations at start of text vs middle of text
- Abbreviations after newlines

## Known Limitations

1. **Single word only**: Only the last word in the typed buffer is checked; multi-word abbreviations are not supported
2. **Space trigger only**: Abbreviations only expand when followed by a space
3. **No undo**: Once expanded, there's no easy way to revert to the abbreviation
4. **No sync**: Abbreviations are stored locally only; no cloud backup/sync
5. **Single $0 placeholder**: Only the first `$0` is used if multiple exist in expansion

## Contributing

When contributing to this feature:

1. Follow existing code style (Kotlin, ktlint)
2. Add string resources for any new UI text
3. Include database migrations for schema changes
4. Test on multiple Android versions
5. Update this documentation for significant changes
