package com.example.couplesnight.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.couplesnight.model.Code
import com.example.couplesnight.model.CustomMovie
import com.example.couplesnight.model.Preference

@Database(entities = [Code::class, Preference::class, CustomMovie::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun codeDao(): CodeDao
    abstract fun preferenceDao(): PreferenceDao
    abstract fun customMovieDao(): CustomMovieDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "couples_night_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
