# Upstream merge — 2026-11-26

Merged `dessalines/thumb-key` upstream into the fork (`main`).

- Range: `87acd5b9..131ad5ab` (5 commits)
- Upstream target: `131ad5ab`
- Resulting app version: unchanged from the fork
- Room database: version 33 → 34 through the fork's `MIGRATION_33_34`; upstream migration numbers were shifted to preserve the fork's existing migrations.
- The merge remains staged for review and uncommitted; the watcher will create the merge commit.

## Incoming commits

- `905903ba` — Launch FUTO and other voice IMEs from the microphone swipe.
- `11fdd39d` — Fix key hitbox dead zones.
- `eb2a57f3` — Add the Spanish translation line.
- `fcdb5b20` — Add floating mode, keyboard opacity, key animations, and a swipe trail.
- `131ad5ab` — Update the Compose BOM to `2026.11.01`.

## Meaningful upstream changes

- Added voice IME selection that prioritizes voice subtypes and falls back to recognized voice IME names.
- Moved key gesture handling ahead of visual padding and decoration so edge taps register.
- Added a draggable floating keyboard with persisted position and a configurable handle location.
- Added keyboard opacity, released-key animation choices, and an optional swipe trail.
- Added the upstream Spanish string and English strings for the new controls.
- Updated the Compose BOM to the upstream version `2026.11.01`.

## Conflicts resolved

- Kept the fork side for structural conflicts in `ComposeKeyboardView.kt`, `IMEService.kt`, `db/Migrations.kt`, `KeyboardKey.kt`, `KeyboardScreen.kt`, `BackupAndRestoreScreen.kt`, `LookAndFeelScreen.kt`, `utils/Types.kt`, and `utils/Utils.kt`, then ported upstream behavior into the fork's corresponding files. Removed upstream's monolithic `db/AppDb.kt` in favor of the fork's split database files.
- Kept the fork's app identity, version, dependencies, settings screens, keyboard layouts, and clipboard behavior while adapting upstream's floating keyboard and animation files to the fork structure.
- Split the floating frame and window behavior into smaller files to follow the repository's Kotlin file-size convention.

## Stored settings and defaults

- Existing saved layout choices and look-and-feel settings are retained by the version 34 migration. The six new columns receive migration defaults.
- New installs and reset-to-defaults use 100% keyboard opacity, a bottom floating handle, centered horizontal and bottom vertical floating position, the first key animation, and a disabled swipe trail.

## Fork features verified intact

- `DETypeSplitBen` remains registered in `KeyboardLayout.kt`.
- The Abbreviations screen remains present.
- Clipboard mode, sketch, suggestions, and clipboard key behavior remain wired into the shared keyboard screen.

## Verification

- Focused `LookAndFeelFormTest` passed, covering new defaults and round-tripping the four look-and-feel controls.
- The planned `run-gradle.bat build -x lint` generated schema version 34, but did not finish successfully: Kotlin lint tasks reported formatting and import-order findings in files changed during the earlier checkpoints. The watcher owns the post-implementation review and fix phases.
- The final on-device floating keyboard, animation, voice switching, key-edge, and database-upgrade checks remain for the human.
