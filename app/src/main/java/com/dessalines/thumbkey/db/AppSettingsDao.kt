package com.dessalines.thumbkey.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AbbreviationDao {
    @Query("SELECT * FROM Abbreviation")
    fun getAllAbbreviations(): LiveData<List<Abbreviation>>

    @Query("SELECT * FROM Abbreviation WHERE lower(abbreviation) = lower(:abbr) LIMIT 1")
    fun getAbbreviation(abbr: String): Abbreviation?

    @Query("SELECT * FROM Abbreviation WHERE lower(abbreviation) = lower(:abbr) LIMIT 1")
    fun observeAbbreviation(abbr: String): Flow<Abbreviation?>

    @Query("SELECT * FROM Abbreviation WHERE lower(abbreviation) = lower(:abbr) LIMIT 1")
    suspend fun getAbbreviationAsync(abbr: String): Abbreviation?

    @Query("UPDATE Abbreviation SET abbreviation = :newAbbr, expansion = :expansion WHERE id = :id")
    suspend fun update(
        id: Int,
        newAbbr: String,
        expansion: String,
    )

    @Query("INSERT INTO Abbreviation (abbreviation, expansion) VALUES (:abbr, :expansion)")
    suspend fun insert(
        abbr: String,
        expansion: String,
    )

    @Query("DELETE FROM Abbreviation WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface WordUsageDao {
    @Query(
        "SELECT * FROM WordUsage WHERE ignored = 0 AND word_key LIKE :prefixKey || '%' ESCAPE '\\' AND word_key != :prefixKey ORDER BY use_count DESC, last_used DESC LIMIT :limit",
    )
    fun suggest(
        prefixKey: String,
        limit: Int,
    ): Flow<List<WordUsage>>

    @Query("SELECT * FROM WordUsage WHERE ignored = 0 ORDER BY last_used DESC LIMIT :limit")
    fun recent(limit: Int): Flow<List<WordUsage>>

    @Query("SELECT * FROM WordUsage WHERE ignored = 0 ORDER BY use_count DESC, last_used DESC")
    fun all(): Flow<List<WordUsage>>

    @Query("UPDATE WordUsage SET use_count = use_count + 1, last_used = :now, word = :word WHERE word_key = :key AND ignored = 0")
    suspend fun updateUse(
        word: String,
        key: String,
        now: Long,
    ): Int

    @Query("INSERT OR IGNORE INTO WordUsage (word, word_key, use_count, last_used) VALUES (:word, :key, 1, :now)")
    suspend fun insertUse(
        word: String,
        key: String,
        now: Long,
    )

    @Transaction
    // Ignored words are skipped by the unique word_key index and INSERT OR IGNORE.
    suspend fun recordUse(
        word: String,
        key: String,
        now: Long,
    ) {
        if (updateUse(word, key, now) == 0) insertUse(word, key, now)
    }

    @Query("UPDATE WordUsage SET ignored = 1 WHERE id = :id")
    suspend fun ignoreById(id: Int)

    @Query("SELECT COUNT(*) FROM WordUsage WHERE ignored = 1")
    fun ignoredCount(): Flow<Int>

    @Query("DELETE FROM WordUsage WHERE ignored = 1")
    suspend fun deleteIgnored()

    @Query("SELECT * FROM WordUsage WHERE id = :id")
    suspend fun getById(id: Int): WordUsage?

    @Query("SELECT * FROM WordUsage WHERE word_key = :key AND id != :id LIMIT 1")
    suspend fun getByKeyExceptId(
        key: String,
        id: Int,
    ): WordUsage?

    @Query("UPDATE WordUsage SET word = :word, word_key = :key WHERE id = :id")
    suspend fun updateWord(
        id: Int,
        word: String,
        key: String,
    )

    @Query("UPDATE WordUsage SET use_count = use_count + :useCount, last_used = MAX(last_used, :lastUsed), word = :word WHERE id = :id")
    suspend fun mergeInto(
        id: Int,
        useCount: Int,
        lastUsed: Long,
        word: String,
    )

    @Query("DELETE FROM WordUsage WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Transaction
    suspend fun rename(
        id: Int,
        word: String,
        key: String,
    ) {
        val existing = getById(id) ?: return
        if (word.isBlank()) {
            deleteById(id)
            return
        }
        val clash = getByKeyExceptId(key, id)
        if (clash == null) {
            updateWord(id, word, key)
        } else {
            mergeInto(clash.id, existing.useCount, existing.lastUsed, word)
            deleteById(id)
        }
    }
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM AppSettings limit 1")
    fun getSettings(): LiveData<AppSettings>

    @Query("SELECT * FROM AppSettings limit 1")
    fun getSettingsSync(): AppSettings?

    @Update
    suspend fun updateAppSettings(appSettings: AppSettings)

    @Update(entity = AppSettings::class)
    fun updateLayouts(layouts: LayoutsUpdate)

    @Update(entity = AppSettings::class)
    fun updateLookAndFeel(lookAndFeel: LookAndFeelUpdate)

    @Update(entity = AppSettings::class)
    fun updateBehavior(behavior: BehaviorUpdate)

    @Update(entity = AppSettings::class)
    fun updateKeyModifications(behavior: KeyModificationsUpdate)

    @Update(entity = AppSettings::class)
    fun updateClipboardSettings(clipboardSettings: ClipboardSettingsUpdate)

    @Update(entity = AppSettings::class)
    fun updateOtherSettings(otherSettings: OtherSettingsUpdate)

    @Query("UPDATE AppSettings SET last_version_code_viewed = :versionCode")
    suspend fun updateLastVersionCode(versionCode: Int)

    @Query("UPDATE AppSettings SET abbreviation_buffer_enabled = :enabled WHERE id = 1")
    fun updateAbbreviationBufferEnabled(enabled: Int)

    @Query("UPDATE AppSettings SET word_suggestions_enabled = :enabled WHERE id = 1")
    fun updateWordSuggestionsEnabled(enabled: Int)

    @Query("UPDATE AppSettings SET word_combinations_enabled = :enabled WHERE id = 1")
    fun updateWordCombinationsEnabled(enabled: Int)
}

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
