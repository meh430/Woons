package com.mehul.woons.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

// This will be the result when searching or browsing a category
// Also will be cached for discover
@Entity(tableName = "DiscoverCache")
data class WebtoonsPage(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var category: String = "",
    @Ignore val currentPage: Int = 1,
    @Ignore val lastPage: Int = 1,
    var items: List<Webtoon> = listOf()
)