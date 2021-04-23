package com.mehul.woons.repositories

import androidx.lifecycle.LiveData
import com.mehul.woons.entities.Chapter
import com.mehul.woons.local.ReadChaptersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReadChaptersRepository @Inject constructor(val readChaptersDao: ReadChaptersDao) {
    suspend fun getReadChapters(webtoonId: Long): LiveData<List<Chapter>> =
        withContext(Dispatchers.IO) {
            readChaptersDao.getReadChapters(webtoonId)
        }

    suspend fun deleteReadChapter(id: Long) = withContext(Dispatchers.IO) {
        readChaptersDao.deleteReadChapter(id)
    }

    suspend fun deleteManyReadChapters(chapterIds: List<Long>) = withContext(Dispatchers.IO) {
        readChaptersDao.deleteManyReadChapters(chapterIds)
    }

    suspend fun insertReadChapter(chapter: Chapter) = withContext(Dispatchers.IO) {
        readChaptersDao.insertReadChapter(chapter)
    }

    suspend fun insertAllReadChapters(chapters: List<Chapter>) = withContext(Dispatchers.IO) {
        readChaptersDao.insertAllReadChapters(chapters)
    }
}