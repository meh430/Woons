package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Chapter
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.entities.WebtoonChapters
import com.mehul.woons.getUpdatedAllChapters
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.ReadChaptersRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebtoonInfoViewModel(application: Application) : AndroidViewModel(application) {
    // Injected data sources
    @Inject
    lateinit var libraryRepository: LibraryRepository

    @Inject
    lateinit var chaptersRepository: ReadChaptersRepository

    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    // to track removals and additions to library
    var webtoonIdLive = MutableLiveData<Long>()
    var inLibrary = false

    // Data from api
    var webtoonInfo = MutableLiveData<Resource<WebtoonChapters>>()

    // Chapters read gotten from room
    // Listen for change to update all chapters
    val readChapters = Transformations.switchMap(webtoonIdLive) {
        liveData<List<Chapter>> {
            if (it != LibraryRepository.NOT_IN_LIBRARY) {
                chaptersRepository.getReadChapters(it)
            } else {
                ArrayList<Chapter>()
            }
        }
    }

    // modified data from api
    var allChapters = MutableLiveData<Resource<List<Chapter>>>()

    init {
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
                chaptersRepository,
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
                chaptersRepository,
                inLibrary,
                webtoonIdLive.value!!,
                webtoonInfo.value!!.data!!.chapters
            )
            allChapters.value = Resource.success(allChaptersResult)
        }
    }

    // all funcs below require webtoon info to be loaded
    fun addToLibrary() {
        if (!inLibrary) {
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
        }
    }

    fun markSingleRead(position: Int) {
        if (!inLibrary) {
            return
        }

        viewModelScope.launch {
            val markedCh = allChapters.value!!.data!![position].copy()
            if (!markedCh.hasRead) {
                //markedCh.id = 0
                //markedCh.webtoonId = webtoonIdLive.value!!
                chaptersRepository.insertReadChapter(
                    Chapter(
                        webtoonId = webtoonIdLive.value!!,
                        chapterNumber = markedCh.chapterNumber,
                        uploadDate = markedCh.uploadDate
                    )
                )
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
                    //val ch = it.copy()
                    //ch.id = 0
                    //ch.webtoonId = webtoonIdLive.value!!
                    //ch
                    Chapter(
                        webtoonId = webtoonIdLive.value!!,
                        chapterNumber = it.chapterNumber,
                        uploadDate = it.uploadDate
                    )
                }
            chaptersRepository.insertAllReadChapters(markedChapters)
        }
    }

    fun markSingleUnread(position: Int) {
        if (!inLibrary) {
            return
        }

        viewModelScope.launch {
            val markedCh = allChapters.value!!.data!![position]
            if (markedCh.hasRead) {
                chaptersRepository.deleteReadChapter(markedCh.id)
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
            chaptersRepository.deleteManyReadChapters(markedChapters)
        }
    }
}