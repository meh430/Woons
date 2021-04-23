package com.mehul.woons.entities

// Will be retrofit response from api, in infoVm
// Update chapters isRead field after comparison with read chapters!
data class WebtoonChapters(
    val webtoon: Webtoon,
    val chapters: ArrayList<Chapter>
)