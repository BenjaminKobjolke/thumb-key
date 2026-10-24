# Version
2

# Coding Rules (Pointer)

This project's rules live in two files in the project root:

- `CODING_RULES.md` — code quality and conventions. BINDING for all code work in
  this repository, always.
- `IMPLEMENTATION_FLOW.md` — the end-to-end flow to follow when planning and
  implementing a change (checks, gates, Definition of Done).

MANDATORY: Before writing or editing ANY code, you MUST Read BOTH files in full
**in the current session**. Do not rely on memory of a previous session, a
summary, or partial reads.

If you are about to make a code change and have not read both files in this
session: STOP, read them, then continue.

An external tool may orchestrate the implementation itself. When a skill or run
states that the implementation is orchestrated, ignore `IMPLEMENTATION_FLOW.md`
entirely — the orchestrator runs those steps as its own phases. `CODING_RULES.md`
stays binding either way.

Do not inline rules back into this file and do not use `@import` for either file —
they are intentionally referenced, not imported.


# Project Instructions

## Code directory

This project's code directory is `app/` — graphify builds and queries are scoped to it.

## Keyboard changes

When the user asks for changes to "the keyboard" / "my keyboard", the file to edit is:

`app/src/main/java/com/dessalines/thumbkey/keyboards/DETypeSplitBen.kt`

It defines the `deutsch type-split-ben` layout in three modes: `KB_DE_TYPESPLITBEN_MAIN`,
`KB_DE_TYPESPLITBEN_SHIFTED`, `KB_DE_TYPESPLITBEN_NUMERIC`. Apply matching changes across the
relevant modes (main + shifted usually mirror each other).

## Code Analysis

Run `tools/analyze_changed_and_new_files.bat` after implementation work. It checks changed and
new Kotlin files with the shared CLI code analyzer and returns a non-zero exit code for configured
findings. Use `tools/analyze_code.bat` for a full source-file audit. The analyzer location is set
in `tools/analyze_code_config.bat`.
