package com.mehul.woons.DI

import android.app.Application
import com.mehul.woons.viewmodels.BrowseViewModel
import com.mehul.woons.viewmodels.DiscoverViewModel
import com.mehul.woons.viewmodels.LibraryViewModel
import com.mehul.woons.viewmodels.WebtoonInfoViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// Component for injecting fields into viewmodels
@Singleton
@Component(modules = [ProviderModule::class])
interface AppComponent {
    // field injectors
    fun injectIntoLibrary(libraryViewmodel: LibraryViewModel)
    fun injectIntoInfo(webtoonInfoViewModel: WebtoonInfoViewModel)
    fun injectIntoDiscover(discoverViewModel: DiscoverViewModel)
    fun injectIntoBrowse(browseViewModel: BrowseViewModel)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}