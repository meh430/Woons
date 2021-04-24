package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Chapter
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.entities.WebtoonChapters
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.ReadChaptersRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WebtoonInfoViewModel(
    application: Application,
    val name: String, // actual name
    val internalName: String, // api name
    val webtoonId: Long // room id
) :
    AndroidViewModel(application) {
    // Injected data sources
    @Inject
    lateinit var libraryRepository: LibraryRepository

    @Inject
    lateinit var chaptersRepository: ReadChaptersRepository

    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    // to track removals and additions to library
    var webtoonIdLive = MutableLiveData<Long>()
    val inLibrary = MutableLiveData<Boolean>()

    // Data from api
    var webtoonInfo = MutableLiveData<Resource<WebtoonChapters>>()

    // Chapters read gotten from room
    val readChapters = Transformations.switchMap(webtoonIdLive) {
        liveData<List<Chapter>> {
            chaptersRepository.getReadChapters(webtoonId)
        }
    }

    // modified data from api
    var allChapters = MutableLiveData<Resource<List<Chapter>>>()

    init {
        webtoonIdLive.value = webtoonId
        inLibrary.value = webtoonId != LibraryRepository.NOT_IN_LIBRARY
        startLoading()
        (application as WoonsApplication).appComponent.injectIntoInfo(this)
    }

    private fun startLoading() {
        webtoonInfo.value = Resource.loading()
        allChapters.value = Resource.loading()
    }

    fun initializeData() {
        viewModelScope.launch {
            startLoading()
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
            if (inLibrary.value!!) {
                val currWebtoon = webtoonInfo.value!!.data!!.webtoon
                libraryRepository.updateCoverImage(webtoonIdLive.value!!, currWebtoon.coverImage)
                libraryRepository.updateNumChapters(webtoonIdLive.value!!, currWebtoon.numChapters)
            }

            // get all chapters
            val allChaptersResult: List<Chapter> = getUpdatedAllChapters()
            allChapters.value = Resource.success(allChaptersResult)
        }
    }

    // Requires webtoon info to be loaded
    suspend fun getUpdatedAllChapters() = withContext(Dispatchers.Default) {
        val readNamesMap =
            readChapters.value!!.map { it.internalChapterReference to it }.toMap()
        val currAllChapters = webtoonInfo.value!!.data!!.chapters
        // Update whether has read and add ids to available ones
        for (ch in currAllChapters) {
            val hasRead = readNamesMap.containsKey(ch.internalChapterReference)
            ch.hasRead = hasRead
            ch.id = readNamesMap[ch.internalChapterReference]?.id ?: 0
        }
        currAllChapters
    }

    // Whenever there is a change in read chapters, call this so all chapters can be updated
    fun updateAllChapters() {
        viewModelScope.launch {
            allChapters.value = Resource.loading()
            val allChaptersResult: List<Chapter> = getUpdatedAllChapters()
            allChapters.value = Resource.success(allChaptersResult)
        }
    }

    // requires webtoon info was loaded
    fun addToLibrary() {
        viewModelScope.launch {
            val currWebtoon = webtoonInfo.value!!.data!!.webtoon
            val insertId = libraryRepository.insertWebtoon(
                Webtoon(
                    name = currWebtoon.name,
                    internalName = currWebtoon.internalName,
                    coverImage = currWebtoon.coverImage,
                    numChapters = currWebtoon.numChapters,
                    numRead = currWebtoon.numRead
                )
            )

            inLibrary.value = true
            webtoonIdLive.value = insertId
        }
    }

    // Requires webtoon info was loaded
    fun removeFromLibrary() {
        if (!inLibrary.value!!) {
            return
        }
        viewModelScope.launch {
            libraryRepository.deleteWebtoon(webtoonIdLive.value!!)
        }
    }

    fun markSingleRead() {

    }

    fun markManyRead() {

    }

    fun markSingleUnread() {

    }

    fun markManyUnread() {

    }
}