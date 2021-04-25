package com.mehul.woons.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mehul.woons.entities.Webtoon

// get all the webtoons in library
// get a specific webtoon by name to check if exists
// add to library
// remove from library
// update coverimage
// update numread
// update numchapters
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
}