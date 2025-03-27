package com.example.couplesnight.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPreferences(preferences: Preferences)

    @Query("SELECT * FROM preferences WHERE userId = :userId")
    suspend fun getPreferences(userId: String): Preferences?
}