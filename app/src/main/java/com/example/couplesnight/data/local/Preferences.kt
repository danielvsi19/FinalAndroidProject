package com.example.couplesnight.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class Preferences(
    @PrimaryKey val userId: String,
    val selectedGenres: List<String>,
    val minRating: Float,
    val streamingPlatforms: List<String>
)