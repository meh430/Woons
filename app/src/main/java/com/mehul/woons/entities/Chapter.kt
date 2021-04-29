package com.mehul.woons.entities

import androidx.room.*

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