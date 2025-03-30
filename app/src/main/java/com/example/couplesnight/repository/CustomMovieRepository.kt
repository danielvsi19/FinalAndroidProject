package com.example.couplesnight.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.couplesnight.database.AppDatabase
import com.example.couplesnight.model.CustomMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomMovieRepository(application: Application) {

    private val customMovieDao = AppDatabase.getDatabase(application).customMovieDao()

    // Get all custom movies
    fun getAllCustomMovies(): LiveData<List<CustomMovie>> {
        return customMovieDao.getAll()
    }

    // Insert a custom movie
    suspend fun insertCustomMovie(customMovie: CustomMovie) {
        withContext(Dispatchers.IO) {
            customMovieDao.insert(customMovie)
        }
    }

    // Update a custom movie
    suspend fun updateCustomMovie(customMovie: CustomMovie) {
        withContext(Dispatchers.IO) {
            customMovieDao.update(customMovie)
        }
    }

    // Delete a custom movie
    suspend fun deleteCustomMovie(customMovie: CustomMovie) {
        withContext(Dispatchers.IO) {
            customMovieDao.delete(customMovie)
        }
    }
}
