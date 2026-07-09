# Upstream merge — 2026-07-09

Merged `dessalines/thumb-key` upstream into the fork (`main`).

- Range: `ccdf3e66..a4375338` (~51 commits)
- Merge commit: `452c121d`
- Upstream reached version **5.1.11** (versionCode 181)
- Build: `build_debug_apk.bat` → BUILD SUCCESSFUL

## New keyboard layouts

- **Bengali** (`BNThumbKey.kt`) — #1931
- **Thumb-Key Wide Compose** (`ENThumbKeyWideCompose.kt`) — #1906
- **2 flipped layouts** (`ENMessagEaseComposeFlipped.kt`, `FRMessagEaseFlipped.kt`) — #1921
- **DE TypeSplit Suave** (`DETypeSplitSuave.kt`) — #1890
- **Sakha (sah)** based on Thumb-Key Writer (`SAHThumbKeyWriter.kt`) — #1879

## Layout improvements

- European layout: added Turkish **ı** and **İ** — #1888
- European layout: normalized characters — #1828 / #1873
- Malay `MYThumbKey.kt` refinements — #1935, #1937, #1938

## Fixes

- **Fixed database import/export** — #1894 (uses `room-db-export-import` 0.1.1)

## Other

- New Burmese (my) translation + many Weblate translation updates (de, es, fr, ru, sl, ar, bg, …)
- Renovate dependency/plugin bumps: Kotlin 2.4.0, KSP, AGP 9.2.0, Gradle 9.6.1,
  compose-bom 2026.06.01, arrow-kt 2.2.3, navigation-compose, runtime-livedata, etc.

## Conflict resolved

- `KeyboardScreen.kt` — upstream restructured the emoji picker (styled `Box` +
  bottom-row keys). Took upstream's version and re-applied the fork's abbreviation-buffer
  wiring: `performKeyAction` in the emoji handler + `abbreviationBufferEnabled` on all
  `KeyboardKey(...)` calls.

## Fork features verified intact

- Abbreviations (buffer tracking, `AbbreviationsScreen.kt`)
- `DETypeSplitBen` custom German split layout
- `onToggleClipboardMode` / `onToggleHideLetters` callbacks
