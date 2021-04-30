package com.mehul.woons.repositories

import com.mehul.woons.entities.WebtoonChapters
import com.mehul.woons.entities.WebtoonsPage
import com.mehul.woons.remote.WebtoonApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WebtoonApiRepository @Inject constructor(private val webtoonApi: WebtoonApi) {

    suspend fun getWebtoons(
        page: Int,
        category: String
    ): WebtoonsPage = withContext(Dispatchers.IO) {
        webtoonApi.getWebtoons(page, category)
    }

    suspend fun searchWebtoons(query: String): WebtoonsPage = withContext(Dispatchers.IO) {
        webtoonApi.searchWebtoons(query)
    }

    suspend fun getWebtoonInfo(internalName: String): WebtoonChapters =
        withContext(Dispatchers.IO) {
            webtoonApi.getWebtoonInfo(internalName)
        }

    suspend fun getWebtoonChapter(
        internalName: String,
        internalChapterReference: String
    ): List<String> = withContext(Dispatchers.IO) {
        webtoonApi.getWebtoonChapter(internalName, internalChapterReference)
    }

    suspend fun getDiscover(): List<WebtoonsPage> = withContext(Dispatchers.IO) {
        webtoonApi.getDiscover()
    }
}