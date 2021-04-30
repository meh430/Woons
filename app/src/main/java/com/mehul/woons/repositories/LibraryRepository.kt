package com.mehul.woons.repositories

import androidx.lifecycle.LiveData
import com.mehul.woons.entities.Chapter
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.local.LibraryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LibraryRepository @Inject constructor(private val libraryDao: LibraryDao) {
    suspend fun getLibraryWebtoons(): LiveData<List<Webtoon>> = withContext(Dispatchers.IO) {
        libraryDao.getLibraryWebtoons()
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

    suspend fun deleteAllWebtoons() = withContext(Dispatchers.IO) {
        libraryDao.deleteAllWebtoons()
    }

    suspend fun getNonLiveReadChapters(webtoonId: Long) = withContext(Dispatchers.IO) {
        libraryDao.getNonLiveReadChapters(webtoonId)
    }

    suspend fun deleteReadChapter(id: Long) = withContext(Dispatchers.IO) {
        libraryDao.deleteReadChapter(id)
    }

    suspend fun deleteManyReadChapters(chapterIds: List<Long>) = withContext(Dispatchers.IO) {
        libraryDao.deleteManyReadChapters(chapterIds)
    }

    suspend fun insertReadChapter(chapter: Chapter) = withContext(Dispatchers.IO) {
        libraryDao.insertReadChapter(chapter)
    }

    suspend fun insertAllReadChapters(chapters: List<Chapter>) = withContext(Dispatchers.IO) {
        libraryDao.insertManyReadChapters(chapters)
    }

    companion object {
        const val NOT_IN_LIBRARY: Long = -1
    }
}