package com.mehul.woons.remote

import com.mehul.woons.entities.WebtoonChapters
import com.mehul.woons.entities.WebtoonsPage
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// what the fuck is this bullshit o my
interface WebtoonApi {
    @GET("available-categories")
    suspend fun getAvailableCategories(): List<String>

    @GET("webtoons/{page}/{category}")
    suspend fun getWebtoons(
        @Path("page") page: Int,
        @Path("category") category: String
    ): WebtoonsPage

    @GET("search")
    suspend fun searchWebtoons(@Query("q") query: String): WebtoonsPage

    @GET("webtoon/{internalName}")
    suspend fun getWebtoonInfo(@Path("internalName") internalName: String): WebtoonChapters


    @GET("webtoon-chapter/{internalName}/{internalChapterReference}")
    suspend fun getWebtoonChapter(
        @Path("internalName") internalName: String,
        @Path("internalChapterReference") internalChapterReference: String
    ): List<String>

    @GET("discover")
    suspend fun getDiscover(): List<WebtoonsPage>
}