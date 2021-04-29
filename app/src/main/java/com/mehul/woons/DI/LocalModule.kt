package com.mehul.woons.DI

import android.app.Application
import com.google.gson.Gson
import com.mehul.woons.local.DiscoverCacheConverter
import com.mehul.woons.local.DiscoverCacheDatabase
import com.mehul.woons.local.LibraryDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalModule {

    @Singleton
    @Provides
    fun provideGson() = Gson()

    @Singleton
    @Provides
    fun provideReadChaptersDatabase(application: Application) =
        LibraryDatabase.getDatabase(application)

    @Singleton
    @Provides
    fun provideDiscoverCacheDatabase(
        application: Application,
        discoverCacheConverter: DiscoverCacheConverter
    ) =
        DiscoverCacheDatabase.getDatabase(application, discoverCacheConverter)


    @Singleton
    @Provides
    fun provideReadChaptersDao(libraryDatabase: LibraryDatabase) =
        libraryDatabase.readChaptersDao()

    @Singleton
    @Provides
    fun provideDiscoverCacheDao(discoverCacheDatabase: DiscoverCacheDatabase) =
        discoverCacheDatabase.discoverCacheDao()
}