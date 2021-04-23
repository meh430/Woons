package com.mehul.woons.local

import androidx.lifecycle.LiveData
import androidx.room.*
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
    @Query("SELECT * FROM library")
    suspend fun getLibraryWebtoons(): LiveData<List<Webtoon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebtoon(webtoon: Webtoon)

    @Query("DELETE FROM library WHERE id = :id")
    suspend fun deleteWebtoon(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM library where name = :name)")
    suspend fun webtoonWithNameExists(name: String): Boolean

    @Query("UPDATE library SET coverImage = :coverImage")
    suspend fun updateCoverImage(coverImage: String)

    @Query("UPDATE library SET numChapters = :numChapters")
    suspend fun updateNumChapters(numChapters: Int)

    @Query("UPDATE library SET numRead = :numRead")
    suspend fun updateNumRead(numRead: Int)
}