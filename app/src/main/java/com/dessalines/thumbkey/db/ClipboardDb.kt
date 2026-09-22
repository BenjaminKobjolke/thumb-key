package com.dessalines.thumbkey.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

/** Rows of the private clipboard history. */
const val SOURCE_CLIPBOARD = "clipboard"

/** Rows of the Summera transcript history, shown on their own screen and never touched by the clipboard settings. */
const val SOURCE_TRANSCRIPT = "transcript"

@Entity(tableName = "ClipboardItem")
data class ClipboardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_pinned", defaultValue = "0")
    val isPinned: Boolean = false,
    @ColumnInfo(name = "source", defaultValue = "'clipboard'")
    val source: String = SOURCE_CLIPBOARD,
    /** The Summera job id of a transcript, so a restore from the server does not duplicate it. */
    @ColumnInfo(name = "remote_id")
    val remoteId: Int? = null,
)

@Dao
interface ClipboardItemDao {
    @Query("SELECT * FROM ClipboardItem WHERE source = :source ORDER BY is_pinned DESC, timestamp DESC")
    fun getAllClipboardItems(source: String): LiveData<List<ClipboardItem>>

    @Query("SELECT * FROM ClipboardItem WHERE id = :id")
    suspend fun getById(id: Int): ClipboardItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ClipboardItem): Long

    @Update
    suspend fun update(item: ClipboardItem)

    @Delete
    suspend fun delete(item: ClipboardItem)

    @Query("DELETE FROM ClipboardItem WHERE source = :source AND is_pinned = 0")
    suspend fun clearUnpinnedItems(source: String)

    @Query("DELETE FROM ClipboardItem WHERE source = :source")
    suspend fun clearAll(source: String)

    @Query("SELECT COUNT(*) FROM ClipboardItem WHERE source = :source AND is_pinned = 0")
    suspend fun getUnpinnedCount(source: String): Int

    @Query(
        "DELETE FROM ClipboardItem WHERE id IN " +
            "(SELECT id FROM ClipboardItem WHERE source = :source AND is_pinned = 0 ORDER BY timestamp ASC LIMIT :count)",
    )
    suspend fun deleteOldestUnpinned(
        source: String,
        count: Int,
    )

    @Query("DELETE FROM ClipboardItem WHERE source = :source AND is_pinned = 0 AND timestamp < :cutoffTime")
    suspend fun deleteOlderThan(
        source: String,
        cutoffTime: Long,
    )

    @Query("SELECT * FROM ClipboardItem WHERE source = :source AND text = :text LIMIT 1")
    suspend fun findByText(
        source: String,
        text: String,
    ): ClipboardItem?

    @Query("SELECT EXISTS(SELECT 1 FROM ClipboardItem WHERE remote_id = :remoteId)")
    suspend fun existsRemoteId(remoteId: Int): Boolean
}

// Hand-written, not an AutoMigration: app/schemas is git-ignored, so a fresh clone has no 1.json to
// generate it from, and the destructive fallback below would wipe the clipboard history
val CLIPBOARD_MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE ClipboardItem ADD COLUMN source TEXT NOT NULL DEFAULT 'clipboard'")
            db.execSQL("ALTER TABLE ClipboardItem ADD COLUMN remote_id INTEGER")
        }
    }

@Database(
    version = 2,
    entities = [ClipboardItem::class],
    exportSchema = true,
)
abstract class ClipboardDB : RoomDatabase() {
    abstract fun clipboardItemDao(): ClipboardItemDao

    companion object {
        @Volatile
        private var instance: ClipboardDB? = null

        fun getDatabase(context: Context): ClipboardDB =
            instance ?: synchronized(this) {
                val instance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            ClipboardDB::class.java,
                            "clipboard_db",
                        ).addMigrations(CLIPBOARD_MIGRATION_1_2)
                        .fallbackToDestructiveMigration(dropAllTables = true)
                        .build()
                Companion.instance = instance
                instance
            }
    }
}
