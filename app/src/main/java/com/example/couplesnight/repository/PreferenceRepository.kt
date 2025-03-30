package com.example.couplesnight.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.couplesnight.database.AppDatabase
import com.example.couplesnight.model.Preference

class PreferenceRepository(context: Context) {

    private val preferenceDao = AppDatabase.getDatabase(context).preferenceDao()

    fun getAllPreferences(): LiveData<List<Preference>> = preferenceDao.getAllPreferences()

    suspend fun insertPreference(preference: Preference) {
        preferenceDao.insert(preference)
    }

    suspend fun updatePreference(preference: Preference) {
        preferenceDao.update(preference)
    }

    suspend fun deletePreference(preference: Preference) {
        preferenceDao.delete(preference)
    }
}
