package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.WebtoonsPage
import com.mehul.woons.loadRemoteData
import com.mehul.woons.notifyObserver
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
        (application as WoonsApplication).appComponent.injectIntoDiscover(this)
        discoverLists.value = ArrayList()
    }

    // Call after categories is loaded!
    fun updateDiscoverLists() {
        viewModelScope.launch {
            val categs = categories.value!!.data!!
            // Add loadings
            categs.forEach {
                discoverLists.value?.add(Resource.loading(WebtoonsPage(category = it)))
                discoverLists.notifyObserver()
            }

            // Start retrieving data concurrently
            val deferredDiscovers = categs.map { async { getPage(it) } }
            //discoverLists.value?.clear()
            deferredDiscovers.awaitAll().forEachIndexed { index, resource ->
                discoverLists.value!![index] = resource
                discoverLists.notifyObserver()
            }
        }
    }

    suspend fun getPage(category: String): Resource<WebtoonsPage> {
        val currPage = kotlin.runCatching { webtoonApiRepository.getWebtoons(PAGE, category) }
        var retval: Resource<WebtoonsPage> = Resource.loading()
        currPage.onSuccess {
            // make a new page with the correct category in place
            retval = Resource.success(it.copy(category = category))
        }
        currPage.onFailure {
            retval = Resource.error(it.message)
        }

        return retval
    }

    companion object {
        const val PAGE = 1
    }
}