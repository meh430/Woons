package com.mehul.woons.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mehul.woons.entities.Chapter

// get all read chapters for webtoon
// delete read chapter
// delete multiple read chapters
// insert a read chapter
// insert multiple read chapters
@Dao
interface ReadChaptersDao {
    @Query("SELECT * FROM ReadChapters WHERE webtoonId = :webtoonId")
    fun getReadChapters(webtoonId: Long): LiveData<List<Chapter>>

    @Query("DELETE FROM ReadChapters WHERE id = :id")
    suspend fun deleteReadChapter(id: Long)

    @Query("DELETE FROM ReadChapters WHERE id in (:chapterIds)")
    suspend fun deleteManyReadChapters(chapterIds: List<Long>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadChapter(chapter: Chapter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReadChapters(chapters: List<Chapter>)
}