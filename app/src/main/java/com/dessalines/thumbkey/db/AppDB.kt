package com.dessalines.thumbkey.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dessalines.thumbkey.BuildConfig
import java.util.concurrent.Executors

@Database(
    version = 33,
    entities = [AppSettings::class, Abbreviation::class, WordUsage::class, WordPair::class],
    exportSchema = true,
)
abstract class AppDB : RoomDatabase() {
    abstract fun appSettingsDao(): AppSettingsDao

    abstract fun abbreviationDao(): AbbreviationDao

    abstract fun wordUsageDao(): WordUsageDao

    abstract fun wordPairDao(): WordPairDao

    companion object {
        @Volatile
        private var instance: AppDB? = null

        fun getDatabase(context: Context): AppDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return instance ?: synchronized(this) {
                val i =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDB::class.java,
                            "thumbkey",
                        ).allowMainThreadQueries()
                        .addMigrations(*APP_DB_MIGRATIONS)
                        // Necessary because it can't insert data on creation
                        .addCallback(
                            object : Callback() {
                                override fun onOpen(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    Executors.newSingleThreadExecutor().execute {
                                        db.insert(
                                            "AppSettings",
                                            // Ensures it won't overwrite the existing data
                                            CONFLICT_IGNORE,
                                            ContentValues(2).apply {
                                                put("id", 1)
                                                put(
                                                    "show_on_screen_keyboard",
                                                    if (BuildConfig.DEBUG) 1 else DEFAULT_SHOW_ON_SCREEN_KEYBOARD,
                                                )
                                            },
                                        )
                                    }
                                }
                            },
                        ).build()
                instance = i
                // return instance
                i
            }
        }
    }
}
