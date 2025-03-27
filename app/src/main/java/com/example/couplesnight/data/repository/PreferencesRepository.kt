package com.example.couplesnight.data.repository

import com.example.couplesnight.data.local.PreferencesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PreferencesRepository(
    private val preferencesDao: PreferencesDao
) {
    suspend fun savePreferences(preferences: Preferences) {
        withContext(Dispatchers.IO) {
            preferencesDao.upsertPreferences(preferences)
        }
    }

    suspend fun getPreferences(userId: String): Preferences? {
        return withContext(Dispatchers.IO) {
            preferencesDao.getPreferences(userId)
        }
    }
}