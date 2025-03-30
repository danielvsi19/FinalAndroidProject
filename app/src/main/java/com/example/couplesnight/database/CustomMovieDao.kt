package com.example.couplesnight.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.couplesnight.model.CustomMovie

@Dao
interface CustomMovieDao {

    @Insert
    suspend fun insert(customMovie: CustomMovie)

    @Delete
    suspend fun delete(customMovie: CustomMovie)

    @Update
    fun update(customMovie: CustomMovie)

    @Query("SELECT * FROM custom_movie_table")
    fun getAll(): LiveData<List<CustomMovie>>
}
