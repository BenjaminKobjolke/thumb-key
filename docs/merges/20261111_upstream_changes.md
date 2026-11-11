# Upstream merge — 2026-11-11

Merged `dessalines/thumb-key` upstream into the fork (`main`).

- Range: `ed86c0a9..aa56baf6` (8 commits)
- Upstream target: `aa56baf6`
- Resulting app version: 191 / 5.1.21
- Database version: 33 (unchanged; upstream added no Room migration)
- Merge is staged for review; the watcher will create the merge commit.

## Meaningful upstream changes

- **Programming cursor swipes** (#2070): adds Up and Down serializable key actions and routes programming-spacebar swipes through key events, releasing Ctrl and Alt modes.
- **Sketch hint sizing** (#2066): prevents long hint text from enlarging the drawing pad; the hint text shrinks to fit.
- **Hindi extended punctuation** (#2072): adds the Devanagari danda and double danda.
- Gradle 9.9.0, Android Gradle Plugin 9.5.1, Spanish translation, and the version bump were also merged.

## Conflicts resolved

- `keyboards/CommonKeys.kt` — kept the fork's split file; programming spacebar keys remain in `keyboards/SpacebarKeys.kt`, where their Up and Down actions now use `CursorUp` and `CursorDown`.
- `utils/Types.kt` — kept the fork's split types file; the new cursor actions were added to `utils/KeyAction.kt`.
- `utils/KeyboardModificationService.kt` — kept the fork's split service file; Up and Down serializable actions were added to `utils/KeyboardModificationSerializables.kt`.
- `utils/Utils.kt` — kept the fork's split utilities; cursor key-event dispatch was added to `utils/KeyActionTextDispatcher.kt`.
- `ui/components/keyboard/SketchScreen.kt` — kept the fork's screen split; the fitting hint layout was applied in `ui/components/keyboard/SketchPad.kt`.
- Updated the common key-action mapping test for Up and Down and for the programming keys' cursor actions.

## Fork features expected intact

- `DETypeSplitBen` remains registered in `KeyboardLayout.kt`.
- The Abbreviations screen remains present.
- No Room migration was needed; database version remains 33.
