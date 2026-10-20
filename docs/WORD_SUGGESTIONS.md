# Word suggestions

Word suggestions remembers words typed with the Thumb-Key keyboard and offers them in a small bar above the keys. The bar shows up to 50 matches, ordered by use count and then recency. With no partial word, it shows the most recently used words. Swipe sideways to see more suggestions. Tapping a suggestion replaces the partial word and inserts a space. Long-press a suggestion to edit or delete it.

## Learning and privacy

A word is learned when a space or Enter (the Return key action, or its long-press new line) follows a word tracked by the keyboard's typed-character buffer. Trailing `.,!?;:"')` punctuation is removed. Words shorter than two characters, words made only of digits, and text containing no letters are ignored. The saved spelling keeps its case, while matching uses a lowercase key. Pasted text, words reached by moving the cursor, and abbreviation expansions are not learned. A word followed only by an app's own send button is not learned.

Learning and the suggestion bar are disabled in password, email and web address fields. Turning off **Show word suggestions** also stops learning; existing words remain saved.

## Settings

Open **Settings > Word suggestions** to turn the feature on or off and manage remembered words. The list shows all remembered words, most used first, with each word's use count. Search is case- and accent-insensitive. A query matches a prefix first, then a substring, then letters found in order with gaps allowed (for example, `mrgn` matches `Morgen`). Tapping a word opens its editor. Saving a blank value deletes the word; changing it to a spelling already in the list merges the counts. The bin button deletes a word directly.

## Storage and code

Words are stored in the `WordUsage` table in the same Room database as app settings and abbreviations. The table stores the displayed spelling, a unique lowercase key, use count and last-used timestamp. Database migration 30→31 creates the table and the `word_suggestions_enabled` setting.

The feature is implemented in `db/AppDb.kt`, `db/Migrations.kt`, `utils/WordSuggestions.kt`, `IMEService.kt`, `ui/components/keyboard/SuggestionBar.kt`, `ui/components/keyboard/KeyboardScreen.kt`, and `ui/screens/WordSuggestionsScreen.kt`. The pure matching and learning helpers are covered by `app/src/test/java/com/dessalines/thumbkey/utils/WordSuggestionsTest.kt`.
