package com.mehul.woons.entities

// This will be the result when searching or browsing a category
data class WebtoonsPage(
    val currentPage: Int,
    val lastPage: Int,
    val items: List<Webtoon>
)