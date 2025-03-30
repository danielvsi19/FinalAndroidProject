package com.example.couplesnight.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.couplesnight.model.Code

@Dao
interface CodeDao {

    @Insert
    suspend fun insert(code: Code)

    @Query("SELECT * FROM code WHERE codeValue = :inputCode LIMIT 1")
    suspend fun getCode(inputCode: String): Code?
}
