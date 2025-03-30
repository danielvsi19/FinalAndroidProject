package com.example.couplesnight.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "custom_movie_table")
data class CustomMovie(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val overview: String,
    val imagePath: String?,
    val isSelected: Boolean = false
) : Parcelable
