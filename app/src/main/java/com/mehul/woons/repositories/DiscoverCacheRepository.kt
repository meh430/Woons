package com.mehul.woons.repositories

import androidx.lifecycle.LiveData
import com.mehul.woons.entities.WebtoonsPage
import com.mehul.woons.local.DiscoverCacheDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DiscoverCacheRepository @Inject constructor(val discoverCacheDao: DiscoverCacheDao) {
    suspend fun clearDiscoverCache() = withContext(Dispatchers.IO) {
        discoverCacheDao.clearDiscoverCache()
    }

    suspend fun getDiscoverCache(): LiveData<List<WebtoonsPage>> = withContext(Dispatchers.IO) {
        discoverCacheDao.getDiscoverCache()
    }

    suspend fun saveDiscoverCache(cache: List<WebtoonsPage>) = withContext(Dispatchers.IO) {
        clearDiscoverCache()
        discoverCacheDao.saveDiscoverCache(cache)
    }
}