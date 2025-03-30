package com.example.couplesnight.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "code")
data class Code(
    @PrimaryKey val codeValue: String
)
