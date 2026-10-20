# Upstream merge — 2026-10-20

Merged `dessalines/thumb-key` upstream into the fork (`main`).

- Range: `24141e07..bbb64613` (23 commits)
- Upstream target: `bbb64613839d553445ae6521d9c78df7a4fe3535`
- Database version: 30 (fork version 29 plus upstream's input-method switching setting)
- Merge is staged for review; the watcher will create the merge commit.

## Meaningful upstream changes

- **Sketch mode** (#2023): draw characters to type them, with look-alikes and candidate search.
- **Searchable layout picker** (#2020): search layouts and pin favorites.
- **Input-method switching setting** (#2041): choose how the keyboard switches to other input methods.
- **Caps-lock legend override** (#2039) and support for `StartComposeCombo` key modifications (#2021).
- **Text processor flush fix** (#2022): flush in `onFinishInputView` to avoid committing composing text twice.
- **Layout identifier spelling** (#2033): renamed seven `Thumbkey` layout identifiers to `ThumbKey`.
- Gradle wrapper update from 9.7.1 to 9.8.1; Google Play links removed.
- Remaining upstream commits are Weblate translations, dependency updates, and version bumps.

## Conflicts resolved

- `IMEService.kt` — kept Summera's keyboard-hidden microphone handling and upstream sketch/input-method-switching state; moved text processor finalization to `onFinishInputView`.
- `AppDb.kt` and `Migrations.kt` — retained the `Abbreviation` entity and fork migration 28→29, renamed upstream's IME setting migration to 29→30, and registered it at database version 30.
- `Types.kt` and `Utils.kt` — retained transcript history and abbreviation expansion alongside sketch actions, upstream compose-combo support, and caps-lock legend behavior.
- `KeyboardKey.kt` and `KeyboardScreen.kt` — wired both transcript-history and sketch callbacks; retained abbreviation buffering on every key and in the emoji picker.
- `strings.xml` — kept Summera strings and added upstream sketch, layout search, and input-method setting strings.
- `gradlew.bat` — used the upstream wrapper script.

## Fork features expected intact

- Summera voice dictation, transcript history, and related settings.
- Abbreviation management and expansion.
- `DETypeSplitBen`, including its voice, clipboard, forward-delete, and numeric layout behavior.
- Clipboard and hide-letters callbacks, plus reset defaults for `vibrateOnSlide` and the new IME setting.

## User-facing notes

- Saved key modifications using the seven old `Thumbkey` identifiers no longer match the renamed layouts. `DETypeSplitBen` is unaffected, and selected layouts remain selected because selection is stored by index.
- Text processors now flush from `onFinishInputView` rather than `onWindowHidden`.
- The database migration adds `switch_ime_mode` while preserving existing settings and abbreviations.
