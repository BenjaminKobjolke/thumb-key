# Upstream merge — 2026-11-20

Merged `dessalines/thumb-key` upstream into the fork (`main`).

- Range: `902443f9..cd10ac78` (1 commit)
- Upstream target: `cd10ac78`
- Resulting app version: 192 / 5.1.22 (unchanged)
- The merge remains staged for review; the watcher will create the merge commit.

## Meaningful upstream changes

- **Keyboard position cycle order** (#2089, issue #2088): cycling now moves through left, center, right, dual, then back to the beginning in each direction.

## Conflicts resolved

- `Utils.kt` — resolved to the fork's version because upstream's edited `performKeyAction` block had already moved out of this file. Fork commit `fc814887` had already ported the new cycle order into `KeyActionModeDispatcher.kt`, so the merge adds no code change.

## Fork features verified intact

- `DETypeSplitBen` remains registered in `KeyboardLayout.kt`.
- The Abbreviations screen remains present.
- The old cycle order is no longer present; the two cycle maps use the upstream order.
