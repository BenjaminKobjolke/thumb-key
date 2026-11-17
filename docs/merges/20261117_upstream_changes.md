# Upstream merge — 2026-11-17

Merged `dessalines/thumb-key` upstream into the fork (`main`).

- Range: `aa56baf6..902443f9` (8 commits)
- Upstream target: `902443f9`
- Resulting app version: 192 / 5.1.22
- The merge remains staged for review; the watcher will create the merge commit.

## Meaningful upstream changes

- **German 4x3 symbol and number layout** (#2077): adds `deutsch thumb-key 4x3 symnum` and registers it in `KeyboardLayout`.
- **Compose combo README example** (#2084): explains adding the compose combo action through layout modification and shows the YAML example.
- Bulgarian translation updates, Gradle 9.9.1, KSP 2.3.14, the Android CI image and Prettier image updates, and the version bump were also merged.

## Conflicts resolved

- `utils/KeyboardLayout.kt` — kept the fork's wildcard keyboard import and appended upstream's new layout entry after `BNThumbKeyAvro`, preserving its upstream comment verbatim. The fork's `DETypeSplitBen` registration remains present.
- `README.md` and `app/build.gradle.kts` auto-merged. The fork's `AcceptBestSuggestion` README entry and dependencies remain alongside upstream's compose combo example and version 192 / 5.1.22.

## Fork features verified intact

- `DETypeSplitBen` remains registered in `KeyboardLayout.kt`.
- The Abbreviations screen remains present.
- The suggestion bar's `AcceptBestSuggestion` action remains documented.
- The new keyboard definition is present once, in `DEThumbKey4x3SymNum.kt`.