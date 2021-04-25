package com.mehul.woons.repositories

import androidx.lifecycle.LiveData
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.local.LibraryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LibraryRepository @Inject constructor(val libraryDao: LibraryDao) {
    suspend fun getLibraryWebtoons(): LiveData<List<Webtoon>> = withContext(Dispatchers.IO) {
        libraryDao.getLibraryWebtoons()
    }

    suspend fun getNumRead(id: Long) = withContext(Dispatchers.IO) {
        libraryDao.getNumRead(id)
    }

    suspend fun insertWebtoon(webtoon: Webtoon) = withContext(Dispatchers.IO) {
        libraryDao.insertWebtoon(webtoon)
    }

    suspend fun deleteWebtoon(id: Long) = withContext(Dispatchers.IO) {
        libraryDao.deleteWebtoon(id)
    }

    suspend fun webtoonWithNameExists(name: String) = withContext(Dispatchers.IO) {
        libraryDao.webtoonWithNameExists(name)
    }

    suspend fun getWebtoonIdFromName(name: String) = withContext(Dispatchers.IO) {
        libraryDao.getWebtoonIdFromName(name)
    }

    suspend fun updateCoverImage(id: Long, coverImage: String) = withContext(Dispatchers.IO) {
        libraryDao.updateCoverImage(id, coverImage)
    }

    suspend fun updateNumChapters(id: Long, numChapters: Int) = withContext(Dispatchers.IO) {
        libraryDao.updateNumChapters(id, numChapters)
    }

    suspend fun updateNumRead(id: Long, numRead: Int) = withContext(Dispatchers.IO) {
        libraryDao.updateNumRead(id, numRead)
    }

    suspend fun deleteAllWebtoons() = withContext(Dispatchers.IO) {
        libraryDao.deleteAllWebtoons()
    }

    companion object {
        const val NOT_IN_LIBRARY: Long = -1
    }
}