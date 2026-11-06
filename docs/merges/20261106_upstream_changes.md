# Upstream merge — 2026-11-06

Merged `dessalines/thumb-key` upstream into the fork (`main`).

- Range: `bbb64613..ed86c0a9` (18 commits)
- Upstream target: `ed86c0a9`
- Resulting app version: 190 / 5.1.20
- Database version: 33 (unchanged; upstream added no Room migration)
- Merge is staged for review; the watcher will create the merge commit.

## Meaningful upstream changes

- **Bangla Avro layout** (#2064): adds the Bengali layout and its Latin-to-Bangla transliteration processor.
- **Hide-letters key modification** (#2062): adds `ToggleHideLetters` to serializable key modifications.
- **Layout search** (#2055): matches both the visible layout title and its identifier.
- **Magma theme** (#2050): adds the Magma palette and theme option.
- **Android IME settings shortcut** (#2060): restores the app settings entry in Android's keyboard settings.
- **Devanagari extended fix** (#2065): removes the apostrophe before Chandrabindu.
- FreeDroidWarn's update warning was removed (#2051). Remaining upstream commits are translations, dependency and build-tool updates, and version bumps.

## Conflicts resolved

- `app/build.gradle.kts` — retained the fork's JUnit and Summera AI dependencies; removed FreeDroidWarn and kept upstream version 190 / 5.1.20.
- `ui/theme/Color.kt` — retained the fork's `pink()` and `green()` palettes and appended upstream's `magma()` palette.
- `ui/theme/Theme.kt` — retained the fork's `colorPairFor` registry and added Magma to its palette map.
- `utils/KeyboardModificationService.kt` — retained the fork's split service file; upstream's serializable action mapping remains in its fork module.
- `ui/components/settings/SettingsScreen.kt` — retained the fork's screen/helper split; title-aware matching was applied in `SettingsComponents.kt`.
- `AvroTextProcessor.kt` — adapted the new processor's cursor callback to the fork's `CursorSelectionUpdate` parameter.
- Updated the theme and key-action mapping tests for Magma and `ToggleHideLetters`.

## Fork features expected intact

- `DETypeSplitBen` remains registered in `KeyboardLayout.kt`.
- The Abbreviations screen remains present.
- Clipboard and hide-letters callbacks remain wired through the keyboard.
- Keyboard key actions still receive the abbreviation-buffer setting through the fork's keyboard configuration objects.
- No Room migration was needed; database version remains 33.
