package com.example.couplesnight.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preference_table")
data class Preference(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val isSelected: Boolean = false
)


