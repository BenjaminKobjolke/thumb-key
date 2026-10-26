package com.dessalines.thumbkey.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(indices = [Index(value = ["previous_key", "next_word_key"], unique = true)])
data class WordPair(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "previous_key") val previousKey: String,
    @ColumnInfo(name = "next_word_key") val nextWordKey: String,
    @ColumnInfo(name = "use_count") val useCount: Int,
    @ColumnInfo(name = "last_used") val lastUsed: Long,
)

@Dao
interface WordPairDao {
    @Query(
        "SELECT WordUsage.* FROM WordPair " +
            "INNER JOIN WordUsage ON WordUsage.word_key = WordPair.next_word_key " +
            "WHERE WordPair.previous_key = :previousKey " +
            "AND WordUsage.ignored = 0 " +
            "AND WordUsage.word_key LIKE :prefixKey || '%' ESCAPE '\\' " +
            "AND WordUsage.word_key != :prefixKey " +
            "ORDER BY WordPair.use_count DESC, WordPair.last_used DESC LIMIT :limit",
    )
    fun nextWords(
        previousKey: String,
        prefixKey: String,
        limit: Int,
    ): Flow<List<WordUsage>>

    @Query(
        "UPDATE WordPair SET use_count = use_count + 1, last_used = :now " +
            "WHERE previous_key = :previousKey AND next_word_key = :nextWordKey",
    )
    suspend fun updateUse(
        previousKey: String,
        nextWordKey: String,
        now: Long,
    ): Int

    @Query(
        "INSERT OR IGNORE INTO WordPair (previous_key, next_word_key, use_count, last_used) " +
            "VALUES (:previousKey, :nextWordKey, 1, :now)",
    )
    suspend fun insertUse(
        previousKey: String,
        nextWordKey: String,
        now: Long,
    )

    @Transaction
    suspend fun recordUse(
        previousKey: String,
        nextWordKey: String,
        now: Long,
    ) {
        if (updateUse(previousKey, nextWordKey, now) == 0) insertUse(previousKey, nextWordKey, now)
    }
}
