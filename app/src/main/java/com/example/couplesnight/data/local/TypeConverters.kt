package com.example.couplesnight.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toList(value: String): List<String> =
        gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
}