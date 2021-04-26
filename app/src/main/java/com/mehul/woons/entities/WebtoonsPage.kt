package com.mehul.woons.entities

// This will be the result when searching or browsing a category
data class WebtoonsPage(
    val category: String = "",
    val currentPage: Int = 1,
    val lastPage: Int = 1,
    val items: List<Webtoon> = listOf()
)