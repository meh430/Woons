package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.WebtoonsPage
import com.mehul.woons.repositories.DiscoverCacheRepository
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class DiscoverViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    @Inject
    lateinit var discoverCacheRepository: DiscoverCacheRepository

    @Inject
    lateinit var libraryRepository: LibraryRepository

    val discoverLists: LiveData<Resource<List<WebtoonsPage>>> = liveData(Dispatchers.IO) {
        emit(Resource.loading<List<WebtoonsPage>>())
        val cache = discoverCacheRepository.getDiscoverCache().map {
            if (it.isNotEmpty()) {
                Resource.success(it)
            } else {
                Resource.loading()
            }
        }
        emitSource(cache)

        // For testing
        //return@liveData

        val cacheExists = discoverCacheRepository.cacheExists()
        // Only update cache if no cache or cache has been saved for over 12 hours (stale)
        if (!cacheExists || getCacheAge() >= 12) {
            Timber.e("Updating cache")
            val discoverResult = kotlin.runCatching { webtoonApiRepository.getDiscover() }
            discoverResult.onFailure {
                emit(Resource.error<List<WebtoonsPage>>(it.message))
                emitSource(cache)
            }
            discoverResult.onSuccess {
                discoverCacheRepository.saveDiscoverCache(it)
            }
        } else {
            Timber.e("Just from cache")
        }
    }

    private suspend fun getCacheAge(): Double {
        val timeStamp = discoverCacheRepository.getNonLiveDiscoverCache()[0].createdAt
        Timber.e((((Date().time - timeStamp).toDouble()) / (1000 * 60 * 60)).toString())
        return ((Date().time - timeStamp).toDouble()) / (1000 * 60 * 60)
    }

    init {
        (application as WoonsApplication).appComponent.injectIntoDiscover(this)
    }

    companion object {
        const val PAGE = 1
    }
}