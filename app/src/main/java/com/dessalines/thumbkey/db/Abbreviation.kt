package com.dessalines.thumbkey.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Abbreviation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "abbreviation") val abbreviation: String,
    @ColumnInfo(name = "expansion") val expansion: String,
)
