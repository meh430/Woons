package com.mehul.woons.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mehul.woons.WoonsApplication
import com.mehul.woons.repositories.WebtoonApiRepository
import javax.inject.Inject

class BrowseViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var webtoonApiRepository: WebtoonApiRepository

    init {
        (application as WoonsApplication).appComponent.injectIntoBrowse(this)
    }
}