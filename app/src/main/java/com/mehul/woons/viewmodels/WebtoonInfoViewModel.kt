package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Chapter
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.entities.WebtoonChapters
import com.mehul.woons.getUpdatedAllChapters
import com.mehul.woons.notifyObserver
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WebtoonInfoViewModel(application: Application) : AndroidViewModel(application) {
    // Injected data sources
    @Inject
    lateinit var libraryRepository: LibraryRepository


    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    // to track removals and additions to library
    var webtoonIdLive = MutableLiveData<Long>()
    var inLibrary = false

    // Data from api
    var webtoonInfo = MutableLiveData<Resource<WebtoonChapters>>()

    val changedRead = MutableLiveData<Boolean>()

    // modified data from api
    var allChapters = MutableLiveData<Resource<List<Chapter>>>()

    init {
        changedRead.value = true
        webtoonIdLive.value = LibraryRepository.NOT_IN_LIBRARY
        startLoading()
        (application as WoonsApplication).appComponent.injectIntoInfo(this)
    }

    private fun startLoading() {
        webtoonInfo.value = Resource.loading()
        allChapters.value = Resource.loading()
    }

    // call first in fragment
    fun initializeInfo(
        name: String, // actual name
        internalName: String // api name
    ) {
        viewModelScope.launch {
            startLoading()
            // check whether webtoon is in library, and update id if there
            if (libraryRepository.webtoonWithNameExists(name)) {
                webtoonIdLive.value = libraryRepository.getWebtoonIdFromName(name)
                inLibrary = true
            } else {
                inLibrary = false
            }


            // Get the webtoon info
            val infoResult =
                kotlin.runCatching { webtoonApiRepository.getWebtoonInfo(internalName) }
            // Issue with network so all fails
            infoResult.onFailure {
                webtoonInfo.value = Resource.error(message = it.message)
                allChapters.value = Resource.error(message = it.message)
                return@launch
            }
            infoResult.onSuccess {
                webtoonInfo.value = Resource.success(data = it)
            }

            // Update stored data if in library?
            if (inLibrary) {
                val currWebtoon = webtoonInfo.value!!.data!!.webtoon
                libraryRepository.updateCoverImage(webtoonIdLive.value!!, currWebtoon.coverImage)
            }

            // get all chapters
            val allChaptersResult: List<Chapter> = getUpdatedAllChapters(
                libraryRepository,
                inLibrary,
                webtoonIdLive.value!!,
                webtoonInfo.value!!.data!!.chapters
            )
            allChapters.value = Resource.success(allChaptersResult)
        }
    }


    // Whenever there is a change in read chapters, call this so all chapters can be updated
    fun updateAllChapters() {
        viewModelScope.launch {
            allChapters.value = Resource.loading()
            val allChaptersResult: List<Chapter> = getUpdatedAllChapters(
                libraryRepository,
                inLibrary,
                webtoonIdLive.value!!,
                webtoonInfo.value!!.data!!.chapters
            )
            allChapters.value = Resource.success(allChaptersResult)
        }
    }

    // all funcs below require webtoon info to be loaded
    fun addToLibrary() {
        if (inLibrary) {
            return
        }

        viewModelScope.launch {
            val currWebtoon = webtoonInfo.value!!.data!!.webtoon
            val insertId = libraryRepository.insertWebtoon(
                Webtoon(
                    name = currWebtoon.name,
                    internalName = currWebtoon.internalName,
                    coverImage = currWebtoon.coverImage
                )
            )

            inLibrary = true
            webtoonIdLive.value = insertId
            changedRead.notifyObserver()
        }
    }

    fun removeFromLibrary() {
        if (!inLibrary) {
            return
        }
        viewModelScope.launch {
            libraryRepository.deleteWebtoon(webtoonIdLive.value!!)
            webtoonIdLive.value = LibraryRepository.NOT_IN_LIBRARY
            inLibrary = false
            changedRead.notifyObserver()
        }
    }

    fun markSingleRead(position: Int) {

        if (!inLibrary) {
            return
        }

        viewModelScope.launch {
            val markedCh = allChapters.value!!.data!![position].copy()

            if (!markedCh.hasRead) {
                libraryRepository.insertReadChapter(
                    Chapter(
                        webtoonId = webtoonIdLive.value!!,
                        chapterNumber = markedCh.chapterNumber,
                        uploadDate = markedCh.uploadDate
                    )
                )
                changedRead.notifyObserver()
            }
        }
    }

    fun markManyRead(cutOff: Int) {
        if (!inLibrary) {
            return
        }
        // Make list slice [cutOff, len)
        // filter out all the read chapters, only marking unread chapters
        // create list of copies
        viewModelScope.launch {
            val allChaptersLength = allChapters.value!!.data!!.size
            val markedChapters =
                allChapters.value!!.data!!.slice(cutOff until allChaptersLength).filter {
                    !it.hasRead
                }.map {
                    Chapter(
                        webtoonId = webtoonIdLive.value!!,
                        chapterNumber = it.chapterNumber,
                        uploadDate = it.uploadDate
                    )
                }
            libraryRepository.insertAllReadChapters(markedChapters)
            changedRead.notifyObserver()
        }
    }

    fun markSingleUnread(position: Int) {
        if (!inLibrary) {
            return
        }

        viewModelScope.launch {
            val markedCh = allChapters.value!!.data!![position]
            if (markedCh.hasRead) {
                libraryRepository.deleteReadChapter(markedCh.id)
                changedRead.notifyObserver()
            }
        }
    }

    fun markManyUnread(cutOff: Int) {
        if (!inLibrary) {
            return
        }

        viewModelScope.launch {
            val allChaptersLength = allChapters.value!!.data!!.size
            val markedChapters =
                allChapters.value!!.data!!.slice(cutOff until allChaptersLength).filter {
                    it.hasRead
                }.map { it.id }
            libraryRepository.deleteManyReadChapters(markedChapters)
            changedRead.notifyObserver()
        }
    }

    // After everything has been loaded? (called init). Make sure that allChapters is success before calling
    // get the first chapter that has not been read yet
    suspend fun getResumeChapter(): Chapter? = withContext(Dispatchers.Default) {
        val chapters = allChapters.value!!.data!!

        // gets first index of chapter that has not been read yet
        val resumeIndex = chapters.indexOfLast { !it.hasRead }
        if (resumeIndex != -1) chapters[resumeIndex] else null
    }

    fun getLastChapter() = webtoonInfo.value!!.data!!.chapters[0]
}