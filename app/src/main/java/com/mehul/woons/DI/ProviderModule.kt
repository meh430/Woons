package com.mehul.woons.DI

import android.app.Application
import com.mehul.woons.local.LibraryDatabase
import com.mehul.woons.local.ReadChaptersDatabase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Provides daos, databases, retrofit, gson, services
@Module
class ProviderModule {

    // TODO: add endpoint
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder().baseUrl("endpoint").addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideLibraryDatabase(application: Application) = LibraryDatabase.getDatabase(application)

    @Singleton
    @Provides
    fun provideReadChaptersDatabase(application: Application) =
        ReadChaptersDatabase.getDatabase(application)

    @Singleton
    @Provides
    fun provideLibraryDao(libraryDatabase: LibraryDatabase) = libraryDatabase.libraryDao()

    @Singleton
    @Provides
    fun provideReadChaptersDao(readChaptersDatabase: ReadChaptersDatabase) =
        readChaptersDatabase.readChaptersDao()
}