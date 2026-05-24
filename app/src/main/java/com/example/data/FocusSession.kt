package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int, // The actual duration focused (usually 1500 + extended)
    val sessionNumber: Int, // e.g. 1 of 4
    val breakSkipped: Boolean = false,
    val extendedSeconds: Int = 0,
    val motivationalQuote: String = ""
)
