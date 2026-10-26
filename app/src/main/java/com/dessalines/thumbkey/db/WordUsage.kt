package com.dessalines.thumbkey.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["word_key"], unique = true)])
data class WordUsage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "word_key") val wordKey: String,
    @ColumnInfo(name = "use_count") val useCount: Int,
    @ColumnInfo(name = "last_used") val lastUsed: Long,
    @ColumnInfo(name = "ignored", defaultValue = "0") val ignored: Int = 0,
)
