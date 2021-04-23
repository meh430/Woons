package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mehul.woons.WoonsApplication
import com.mehul.woons.repositories.LibraryRepository
import com.mehul.woons.repositories.ReadChaptersRepository
import com.mehul.woons.repositories.WebtoonApiRepository
import javax.inject.Inject

class WebtoonInfoViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var libraryRepository: LibraryRepository

    @Inject
    lateinit var chaptersRepository: ReadChaptersRepository

    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    init {
        (application as WoonsApplication).appComponent.injectIntoInfo(this)
    }
}