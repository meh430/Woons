package com.mehul.woons.DI

import android.app.Application
import com.mehul.woons.viewmodels.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// Component for injecting fields into viewmodels
@Singleton
@Component(modules = [LocalModule::class, NetworkModule::class])
interface AppComponent {
    // field injectors
    fun injectIntoLibrary(libraryViewModel: LibraryViewModel)
    fun injectIntoInfo(webtoonInfoViewModel: WebtoonInfoViewModel)
    fun injectIntoDiscover(discoverViewModel: DiscoverViewModel)
    fun injectIntoBrowse(browseViewModel: BrowseViewModel)
    fun injectIntoReader(readerViewModel: ReaderViewModel)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}