package com.mehul.woons.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

// Only really need to get read chapters in infoVm.
// Update hasRead in infovm after getting read chapters from local and all chapters from network
@Entity(
    tableName = "ReadChapters",
    foreignKeys = [ForeignKey(
        entity = Webtoon::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("webtoonId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val webtoonId: Long,
    var chapterNumber: String,
    var uploadDate: String,
    @Ignore var internalChapterReference: String,
    @Ignore var hasRead: Boolean = false
)