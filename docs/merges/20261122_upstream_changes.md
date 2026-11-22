# Upstream merge — 2026-11-22

Merged `dessalines/thumb-key` upstream into the fork (`main`).

- Range: `cd10ac78..87acd5b9` (5 commits)
- Upstream target: `87acd5b9`
- Resulting app version: 192 / 5.1.22 (unchanged)
- The merge remains staged for review; the watcher will create the merge commit.

## Meaningful upstream changes

- Routine version updates: AGP 9.5.2, Gradle 9.9.2, `runtime-livedata` 1.13.0, and `cimg/android` 2026.11.4.

## Conflicts resolved

- None. `app/build.gradle.kts` and `settings.gradle` auto-merged; upstream's version bumps and the fork's dependencies and `:aichatapi` include are all present.

## Fork features verified intact

- `DETypeSplitBen` remains registered in `KeyboardLayout.kt`.
- The Abbreviations screen remains present.
- `:aichatapi` remains included and now builds with AGP 9.5.2 inside the root build.
