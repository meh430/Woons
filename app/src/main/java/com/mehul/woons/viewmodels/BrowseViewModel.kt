package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.entities.WebtoonsPage
import com.mehul.woons.notifyObserver
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class BrowseViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    @Inject
    lateinit var libraryRepository: LibraryRepository

    private var reachedEnd = false
    private var pageNum: Int = DiscoverViewModel.PAGE

    val currentPage: MutableLiveData<Resource<WebtoonsPage>> = MutableLiveData()
    val webtoons: MutableLiveData<ArrayList<Webtoon>> = MutableLiveData()

    init {
        (application as WoonsApplication).appComponent.injectIntoBrowse(this)
        currentPage.value = Resource.loading()
        webtoons.value = ArrayList()
    }

    fun performSearch(query: String) {
        // Ensure that list becomes empty for each search
        viewModelScope.launch {
            currentPage.value = Resource.loading()
            webtoons.value?.clear()
            webtoons.notifyObserver()

            val searchResult = kotlin.runCatching { webtoonApiRepository.searchWebtoons(query) }
            searchResult.onSuccess {
                currentPage.value = Resource.success(it)
                webtoons.value?.addAll(it.items)
                webtoons.notifyObserver()
            }
            searchResult.onFailure {
                currentPage.value = Resource.error(it.message)
            }
        }
    }

    fun performFetch(category: String) {
        if (reachedEnd) {
            return
        }

        Timber.e(pageNum.toString())
        viewModelScope.launch {
            currentPage.value = Resource.loading()
            val fetchResult =
                kotlin.runCatching { webtoonApiRepository.getWebtoons(pageNum, category) }
            fetchResult.onSuccess {
                reachedEnd = pageNum == it.lastPage
                pageNum++
                currentPage.value = Resource.success(it)
                webtoons.value?.addAll(it.items)
                webtoons.notifyObserver()
            }
            fetchResult.onFailure {
                currentPage.value = Resource.error(it.message)
            }
        }
    }
}