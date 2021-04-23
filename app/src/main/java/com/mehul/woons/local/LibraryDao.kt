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
    @Query("SELECT * FROM Library")
    fun getLibraryWebtoons(): LiveData<List<Webtoon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebtoon(webtoon: Webtoon)

    @Query("DELETE FROM Library WHERE id = :id")
    suspend fun deleteWebtoon(id: Long)

    @Query("SELECT EXISTS(SELECT * FROM Library where name = :name)")
    suspend fun webtoonWithNameExists(name: String): Boolean

    @Query("UPDATE Library SET coverImage = :coverImage WHERE id = :id")
    suspend fun updateCoverImage(id: Long, coverImage: String)

    @Query("UPDATE Library SET numChapters = :numChapters WHERE id = :id")
    suspend fun updateNumChapters(id: Long, numChapters: Int)

    @Query("UPDATE Library SET numRead = :numRead WHERE id = :id")
    suspend fun updateNumRead(id: Long, numRead: Int)
}