package com.dessalines.thumbkey.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_21_22 =
    object : Migration(21, 22) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "alter table AppSettings add column abbreviation_buffer_enabled INTEGER NOT NULL default $DEFAULT_ABBREVIATION_BUFFER_ENABLED",
            )
            // Create the Abbreviation table
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS Abbreviation (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    abbreviation TEXT NOT NULL,
                    expansion TEXT NOT NULL
                )
                """.trimIndent(),
            )
        }
    }

val MIGRATION_22_23 =
    object : Migration(22, 23) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "alter table AppSettings add column vibrate_on_slide INTEGER NOT NULL default $DEFAULT_VIBRATE_ON_SLIDE",
            )
        }
    }

val MIGRATION_23_24 =
    object : Migration(23, 24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add clipboard settings columns (ClipboardItem table is in separate database)
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN clipboard_history_enabled INTEGER NOT NULL DEFAULT $DEFAULT_CLIPBOARD_HISTORY_ENABLED",
            )
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN clipboard_auto_cleanup_enabled INTEGER NOT NULL DEFAULT $DEFAULT_CLIPBOARD_AUTO_CLEANUP_ENABLED",
            )
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN clipboard_cleanup_after_minutes INTEGER NOT NULL DEFAULT $DEFAULT_CLIPBOARD_CLEANUP_AFTER_MINUTES",
            )
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN clipboard_size_limit_enabled INTEGER NOT NULL DEFAULT $DEFAULT_CLIPBOARD_SIZE_LIMIT_ENABLED",
            )
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN clipboard_max_size INTEGER NOT NULL DEFAULT $DEFAULT_CLIPBOARD_MAX_SIZE",
            )
        }
    }

val MIGRATION_24_25 =
    object : Migration(24, 25) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN position_padding INTEGER NOT NULL DEFAULT $DEFAULT_POSITION_PADDING",
            )
        }
    }

val MIGRATION_25_26 =
    object : Migration(25, 26) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN use_private_clipboard INTEGER NOT NULL DEFAULT $DEFAULT_USE_PRIVATE_CLIPBOARD",
            )
        }
    }

val MIGRATION_26_27 =
    object : Migration(26, 27) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN show_on_screen_keyboard INTEGER NOT NULL DEFAULT $DEFAULT_SHOW_ON_SCREEN_KEYBOARD",
            )
        }
    }

val MIGRATION_27_28 =
    object : Migration(27, 28) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN slide_hold_enabled INTEGER NOT NULL DEFAULT $DEFAULT_SLIDE_HOLD_ENABLED",
            )
        }
    }

val MIGRATION_28_29 =
    object : Migration(28, 29) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN switch_to_letters_after_space INTEGER NOT NULL DEFAULT $DEFAULT_SWITCH_TO_LETTERS_AFTER_SPACE",
            )
        }
    }

val MIGRATION_29_30 =
    object : Migration(29, 30) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN switch_ime_mode INTEGER NOT NULL DEFAULT $DEFAULT_SWITCH_IME_MODE",
            )
        }
    }

val MIGRATION_30_31 =
    object : Migration(30, 31) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS WordUsage (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    word TEXT NOT NULL,
                    word_key TEXT NOT NULL,
                    use_count INTEGER NOT NULL,
                    last_used INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL("CREATE UNIQUE INDEX index_WordUsage_word_key ON WordUsage (word_key)")
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN word_suggestions_enabled INTEGER NOT NULL DEFAULT $DEFAULT_WORD_SUGGESTIONS_ENABLED",
            )
        }
    }

val MIGRATION_31_32 =
    object : Migration(31, 32) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE WordUsage ADD COLUMN ignored INTEGER NOT NULL DEFAULT 0")
        }
    }

val MIGRATION_32_33 =
    object : Migration(32, 33) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN word_combinations_enabled INTEGER NOT NULL DEFAULT $DEFAULT_WORD_COMBINATIONS_ENABLED",
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS WordPair (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    previous_key TEXT NOT NULL,
                    next_word_key TEXT NOT NULL,
                    use_count INTEGER NOT NULL,
                    last_used INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE UNIQUE INDEX index_WordPair_previous_key_next_word_key ON WordPair (previous_key, next_word_key)",
            )
        }
    }
