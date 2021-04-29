package com.mehul.woons.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Library")
data class Webtoon(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    var name: String = "",
    var internalName: String = "",
    var coverImage: String = "",
    @Ignore var summary: String = "",
    @Ignore var rating: String = "",
    @Ignore var author: String = "",
    @Ignore var artist: String = "",
    @Ignore var numChapters: Int = 0
)