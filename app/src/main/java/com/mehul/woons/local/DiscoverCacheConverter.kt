package com.mehul.woons.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mehul.woons.entities.Webtoon
import javax.inject.Inject

@ProvidedTypeConverter
class DiscoverCacheConverter @Inject constructor(val gson: Gson) {
    @TypeConverter
    fun listToJson(value: List<Webtoon>) = gson.toJson(value)

    @TypeConverter
    fun jsonToList(value: String) =
        gson.fromJson(value, Array<Webtoon>::class.java).toList()
}