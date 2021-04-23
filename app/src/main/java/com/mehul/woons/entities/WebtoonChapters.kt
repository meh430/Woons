package com.mehul.woons.entities

// Will be retrofit response from api, in infoVm
// Update chapters isRead field after comparison with read chapters
// Have allChapters livedata field with transform for read chapters!
// allChapters will be provided to the adapter
data class WebtoonChapters(
    val webtoon: Webtoon,
    val chapters: List<Chapter>
)