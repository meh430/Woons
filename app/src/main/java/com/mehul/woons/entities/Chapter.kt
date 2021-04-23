package com.mehul.woons.entities

import androidx.room.*

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
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    @ColumnInfo(index = true) var webtoonId: Long = 0L,
    var chapterNumber: String = "",
    var uploadDate: String = "",
    @Ignore var internalChapterReference: String = "",
    @Ignore var hasRead: Boolean = false
)