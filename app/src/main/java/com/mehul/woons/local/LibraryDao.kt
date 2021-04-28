package com.mehul.woons.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mehul.woons.entities.Chapter
import com.mehul.woons.entities.Webtoon

// get all read chapters for webtoon
// delete read chapter
// delete multiple read chapters
// insert a read chapter
// insert multiple read chapters
@Dao
interface LibraryDao {
    @Query("SELECT * FROM Library")
    fun getLibraryWebtoons(): LiveData<List<Webtoon>>

    @Query("SELECT * FROM Library")
    suspend fun getNonLiveLibraryWebtoons(): List<Webtoon>

    // Returns insert id
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebtoon(webtoon: Webtoon): Long

    @Query("DELETE FROM Library WHERE id = :id")
    suspend fun deleteWebtoon(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM Library where name = :name)")
    suspend fun webtoonWithNameExists(name: String): Boolean

    @Query("SELECT id FROM library where name = :name")
    suspend fun getWebtoonIdFromName(name: String): Long

    @Query("UPDATE Library SET coverImage = :coverImage WHERE id = :id")
    suspend fun updateCoverImage(id: Long, coverImage: String)

    @Query("DELETE FROM Library")
    suspend fun deleteAllWebtoons()

    @Query("SELECT * FROM ReadChapters WHERE webtoonId = :webtoonId")
    fun getReadChapters(webtoonId: Long): LiveData<List<Chapter>>

    @Query("SELECT * FROM ReadChapters WHERE webtoonId = :webtoonId")
    suspend fun getNonLiveReadChapters(webtoonId: Long): List<Chapter>

    @Query("DELETE FROM ReadChapters WHERE id = :id")
    suspend fun deleteReadChapter(id: Long)

    @Query("DELETE FROM ReadChapters WHERE id in (:chapterIds)")
    suspend fun deleteManyReadChapters(chapterIds: List<Long>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadChapter(chapter: Chapter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManyReadChapters(chapters: List<Chapter>)
}