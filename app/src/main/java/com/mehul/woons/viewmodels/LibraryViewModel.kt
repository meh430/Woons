package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.loadLocalData
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var libraryRepository: LibraryRepository

    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    val initialLoading: MutableLiveData<Boolean> = MutableLiveData()
    val libraryWebtoons: LiveData<Resource<List<Webtoon>>> =
        loadLocalData { libraryRepository.getLibraryWebtoons() }

    init {
        (application as WoonsApplication).appComponent.injectIntoLibrary(this)
        initialLoading.value = true
    }

    // Updates information in room with fresh data from api
    // Run in splash activity!
    fun updateLibraryInfo() {
        initialLoading.value = true
        viewModelScope.launch(Dispatchers.Default) {
            val storedWebtoons = libraryRepository.getLibraryWebtoons().value
            val internalNames = storedWebtoons?.map { it.internalName } ?: ArrayList()
            val ids = storedWebtoons?.map { it.id } ?: ArrayList()
            val freshWebtoons = webtoonApiRepository.getManyWebtoons(internalNames, ids)
            freshWebtoons.forEach {
                val updateCoverImage =
                    async { libraryRepository.updateCoverImage(it.id, it.coverImage) }
                val updateNumChapters =
                    async { libraryRepository.updateNumChapters(it.id, it.numChapters) }
                updateCoverImage.await()
                updateNumChapters.await()
            }
            initialLoading.postValue(false)
        }
    }
}