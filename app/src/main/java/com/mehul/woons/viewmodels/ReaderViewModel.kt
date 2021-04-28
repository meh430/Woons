package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mehul.woons.entities.Chapter
import com.mehul.woons.entities.Resource
import com.mehul.woons.getUpdatedAllChapters
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReaderViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    @Inject
    lateinit var libraryRepository: LibraryRepository


    var currentChapterIndex = 0
    var webtoonId = LibraryRepository.NOT_IN_LIBRARY
    var inLibrary = false

    // Should only be loaded in once, at start
    // note that chapters are in descending order? so like [chapter 5, chapter 4, chapter 3, ...]
    var webtoonChapters: List<Chapter> = ArrayList()

    // links to images of pages
    val chapterPages: MutableLiveData<Resource<List<String>>> = MutableLiveData()

    init {
        chapterPages.value = Resource.loading()
    }

    fun initializeReader(name: String, internalName: String, internalChapterReference: String) {
        viewModelScope.launch {
            // check whether in library
            if (libraryRepository.webtoonWithNameExists(name)) {
                webtoonId = libraryRepository.getWebtoonIdFromName(name)
                inLibrary = true
            } else {
                inLibrary = false
            }

            // load in the info, so we can get a list of all the chapters
            val infoResult =
                kotlin.runCatching { webtoonApiRepository.getWebtoonInfo(internalName) }
            infoResult.onSuccess {
                // get the current index of the chapter
                currentChapterIndex = withContext(Dispatchers.Default) {
                    // Finds the first index of chapter with same internal reference!
                    it.chapters.indexOfFirst { ch ->
                        ch.internalChapterReference.equals(
                            internalChapterReference,
                            true
                        )
                    }
                }

                // update all the chapters with info on whether it has been read or not
                webtoonChapters =
                    getUpdatedAllChapters(libraryRepository, inLibrary, webtoonId, it.chapters)

                // now get the chapter pages
                loadChapterPages(internalName, internalChapterReference)
            }
            infoResult.onFailure {
                // some network error so failed
                // so on error, dont show buttons for prev and next because chapters not loaded!
                chapterPages.value = Resource.error(it.message)
            }
        }
    }

    // call in main ui scope
    // only call after webtoonChapters has been loaded and set up properly
    suspend fun loadChapterPages(internalName: String, internalChapterReference: String) {
        chapterPages.value = Resource.loading()
        val pages = kotlin.runCatching {
            webtoonApiRepository.getWebtoonChapter(internalName, internalChapterReference)
        }
        pages.onFailure {
            chapterPages.value = Resource.error(it.message)
        }
        pages.onSuccess {
            // the pages have been updated to the reader fragment
            chapterPages.value = Resource.success(it)
            // chapter pages should be from currChapterIndex
            // Now, we can consider the chapter to be "read" since we have seen it
            // Only update if in library and hasnt been read before
            if (inLibrary && !webtoonChapters[currentChapterIndex].hasRead) {
                webtoonChapters[currentChapterIndex].hasRead = true
                libraryRepository.insertReadChapter(
                    Chapter(
                        webtoonId = webtoonId,
                        chapterNumber = webtoonChapters[currentChapterIndex].chapterNumber,
                        uploadDate = webtoonChapters[currentChapterIndex].uploadDate
                    )
                )
            }
        }
    }


    // Only call when chapters has been loaded in. Since descending order, next chapter means decrement!
    fun nextChapter(internalName: String): Boolean {
        // cannot decrement anymore!
        if (currentChapterIndex <= 0) {
            return false
        }

        viewModelScope.launch {
            currentChapterIndex--
            loadChapterPages(
                internalName,
                webtoonChapters[currentChapterIndex].internalChapterReference
            )
        }
        return true
    }

    // Only call when chapters has been loaded in. Since descending order, prev chapter means increment!
    fun prevChapter(internalName: String): Boolean {
        // cannot increment anymore!
        if (currentChapterIndex >= webtoonChapters.size - 1) {
            return false
        }

        viewModelScope.launch {
            currentChapterIndex++
            loadChapterPages(
                internalName,
                webtoonChapters[currentChapterIndex].internalChapterReference
            )
        }
        return true
    }


    // ensure that chapters have been loaded before calling
    fun getChapterName(): String {
        return webtoonChapters[currentChapterIndex].chapterNumber
    }
}