package com.mehul.woons.DI

import android.app.Application
import com.mehul.woons.viewmodels.LibraryViewmodel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

// Component for injecting fields into viewmodels
@Singleton
@Component(modules = [ProviderModule::class])
interface AppComponent {
    // field injectors
    fun injectIntoLibrary(libraryViewmodel: LibraryViewmodel)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun application(application: Application): Builder
    }
}