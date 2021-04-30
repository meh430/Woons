package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mehul.woons.WoonsApplication
import com.mehul.woons.entities.Resource
import com.mehul.woons.entities.Webtoon
import com.mehul.woons.loadLocalData
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import javax.inject.Inject

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var libraryRepository: LibraryRepository

    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    val libraryWebtoons: LiveData<Resource<List<Webtoon>>> =
        loadLocalData { libraryRepository.getLibraryWebtoons() }

    init {
        (application as WoonsApplication).appComponent.injectIntoLibrary(this)
    }
}