package com.example.couplesnight.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.couplesnight.model.Preference

@Dao
interface PreferenceDao {

    @Query("SELECT * FROM preference_table")
    fun getAllPreferences(): LiveData<List<Preference>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preference: Preference)

    @Update
    suspend fun update(preference: Preference)

    @Delete
    suspend fun delete(preference: Preference)
}
