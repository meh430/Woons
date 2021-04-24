package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.WebtoonsPage
import com.mehul.woons.loadRemoteData
import com.mehul.woons.repositories.WebtoonApiRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

// resource list of strings that is used for entire fragment loading
// after strings are loaded, start initializing each discover category item!
// A vertical list of horizontal lists!
class DiscoverViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    // Note that categories and discoverLists should have same length!
    val categories = loadRemoteData { webtoonApiRepository.getAvailableCategories() }
    val discoverLists: MutableLiveData<ArrayList<Resource<WebtoonsPage>>> = MutableLiveData()

    init {
        discoverLists.value = ArrayList()
    }

    // Call after categories is loaded!
    fun updateDiscoverLists() {
        viewModelScope.launch {
            val cats = categories.value!!.data!!
            // Add loadings
            cats.forEach {
                discoverLists.value?.add(Resource.loading())
                notifyChange()
            }

            // Start retrieving data concurrently
            val deferredDiscovers = cats.map { async { getPage(it) } }
            deferredDiscovers.awaitAll().forEachIndexed { index, resource ->
                discoverLists.value!![index] = resource
                notifyChange()
            }
        }
    }

    suspend fun getPage(category: String): Resource<WebtoonsPage> {
        val currPage = kotlin.runCatching { webtoonApiRepository.getWebtoons(PAGE, category) }
        var retval: Resource<WebtoonsPage> = Resource.loading()
        currPage.onSuccess {
            retval = Resource.success(it)
        }
        currPage.onFailure {
            retval = Resource.error(it.message)
        }

        return retval
    }

    fun notifyChange() {
        discoverLists.value = discoverLists.value
    }

    companion object {
        const val PAGE = 1
    }
}