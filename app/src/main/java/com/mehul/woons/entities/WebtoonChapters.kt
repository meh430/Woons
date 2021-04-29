package com.mehul.woons.entities

// Will be retrofit response from api, in infoVm
data class WebtoonChapters(
    val webtoon: Webtoon,
    val chapters: List<Chapter>
)