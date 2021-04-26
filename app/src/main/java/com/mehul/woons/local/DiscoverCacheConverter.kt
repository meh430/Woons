package com.mehul.woons.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mehul.woons.entities.Webtoon

class DiscoverCacheConverter {
    @TypeConverter
    fun listToJson(value: List<Webtoon>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) =
        Gson().fromJson(value, Array<Webtoon>::class.java).toList()
}