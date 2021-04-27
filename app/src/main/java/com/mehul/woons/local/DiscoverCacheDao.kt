package com.mehul.woons.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mehul.woons.entities.WebtoonsPage

@Dao
interface DiscoverCacheDao {
    @Query("DELETE FROM DiscoverCache")
    suspend fun clearDiscoverCache()

    @Query("SELECT * FROM DiscoverCache")
    fun getDiscoverCache(): LiveData<List<WebtoonsPage>>

    @Query("SELECT * FROM DiscoverCache")
    fun getNonLiveDiscoverCache(): List<WebtoonsPage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDiscoverCache(cache: List<WebtoonsPage>)
}