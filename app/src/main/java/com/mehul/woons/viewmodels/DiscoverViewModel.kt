package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.WebtoonsPage
import com.mehul.woons.repositories.DiscoverCacheRepository
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

// resource list of strings that is used for entire fragment loading
// after strings are loaded, start initializing each discover category item!
// A vertical list of horizontal lists!
class DiscoverViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    @Inject
    lateinit var discoverCacheRepository: DiscoverCacheRepository

    @Inject
    lateinit var libraryRepository: LibraryRepository

    val discoverLists: LiveData<Resource<List<WebtoonsPage>>> = liveData(Dispatchers.IO) {
        emit(Resource.loading<List<WebtoonsPage>>())
        val cache = discoverCacheRepository.getDiscoverCache().map { Resource.success(it) }
        emitSource(cache)

        // For testing
        return@liveData

        val discoverResult = kotlin.runCatching { webtoonApiRepository.getDiscover() }
        discoverResult.onFailure {
            emit(Resource.error<List<WebtoonsPage>>(it.message))
            emitSource(cache)
        }
        discoverResult.onSuccess {
            discoverCacheRepository.saveDiscoverCache(it)
        }
    }

    init {
        (application as WoonsApplication).appComponent.injectIntoDiscover(this)
    }

    companion object {
        const val PAGE = 1
    }
}