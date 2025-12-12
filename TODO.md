# TODO - Abbreviation Buffer Toggle Feature

## Completed

### Keyboard Layout Changes (DETypeSplitBen.kt)
- [x] Added imports for `android.view.KeyEvent` and `androidx.compose.material.icons.automirrored.outlined.*`
- [x] Backspace key: cursor movement on left/right swipes (←, →), `widthMultiplier = 3`, `slideType = MOVE_CURSOR`
- [x] Spacebar key: delete word on left/right swipes (←×, ×→), `slideType = DELETE`

### Database Changes (AppDb.kt)
- [x] Added `DEFAULT_ABBREVIATION_BUFFER_ENABLED = 1` constant (line 66)
- [x] Added `abbreviationBufferEnabled` column to `AppSettings` entity (lines 284-288)
- [x] Update database version from 21 to 22
- [x] Add `MIGRATION_21_22` to migrations list
- [x] Add `updateAbbreviationBufferEnabled` method to DAO, Repository, and ViewModel

### Migrations.kt
- [x] Added `MIGRATION_21_22` migration at end of file

### AbbreviationsScreen.kt
- [x] Added `AppSettingsViewModel` parameter to function
- [x] Added SwitchPreference toggle for buffer mode
- [x] Toggle updates settings via `updateAbbreviationBufferEnabled` method

### MainActivity.kt
- [x] Pass `appSettingsViewModel` to AbbreviationsScreen

### Utils.kt - performKeyAction function
- [x] Added `abbreviationBufferEnabled: Boolean` parameter
- [x] Modified CommitText handler with conditional logic:
  - If enabled: uses buffer-based approach (current)
  - If disabled: uses `getTextBeforeCursor` approach (old/direct)

### AbbreviationManager.kt
- [x] Added `checkAndExpandText(text: String)` method for direct approach

### KeyboardScreen.kt
- [x] Extract `abbreviationBufferEnabled` setting
- [x] Pass to all KeyboardKey calls

### KeyboardKey.kt
- [x] Added `abbreviationBufferEnabled: Boolean` parameter
- [x] Pass to all performKeyAction calls

### BackupAndRestoreScreen.kt
- [x] Added `abbreviationBufferEnabled` to default settings reset

## Issue Fixed

Abbreviations stopped working after the buffer-based approach was introduced. The toggle allows users to switch between:
- **Buffer approach (new, default enabled)**: Tracks typed characters, more accurate but may have issues
- **Direct approach (old)**: Reads from input connection, simpler and proven to work

## Files Modified

1. `app/src/main/java/com/dessalines/thumbkey/keyboards/DETypeSplitBen.kt` - keyboard layout
2. `app/src/main/java/com/dessalines/thumbkey/db/AppDb.kt` - database schema, DAO, Repository, ViewModel
3. `app/src/main/java/com/dessalines/thumbkey/db/Migrations.kt` - MIGRATION_21_22
4. `app/src/main/java/com/dessalines/thumbkey/ui/screens/AbbreviationsScreen.kt` - toggle UI
5. `app/src/main/java/com/dessalines/thumbkey/MainActivity.kt` - navigation update
6. `app/src/main/java/com/dessalines/thumbkey/ui/components/keyboard/KeyboardScreen.kt` - setting extraction
7. `app/src/main/java/com/dessalines/thumbkey/ui/components/keyboard/KeyboardKey.kt` - parameter threading
8. `app/src/main/java/com/dessalines/thumbkey/utils/Utils.kt` - conditional logic
9. `app/src/main/java/com/dessalines/thumbkey/utils/AbbreviationManager.kt` - new method
10. `app/src/main/java/com/dessalines/thumbkey/ui/components/settings/backupandrestore/BackupAndRestoreScreen.kt` - default reset
