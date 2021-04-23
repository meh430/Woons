package com.mehul.woons.DI

import android.app.Application
import com.mehul.woons.remote.WebtoonApi
import com.mehul.woons.local.LibraryDatabase
import com.mehul.woons.local.ReadChaptersDatabase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Provides daos, databases, retrofit, gson, services
@Module
class ProviderModule {

    // TODO: add endpoint
    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl("endpoint").client(client)
            .addConverterFactory(GsonConverterFactory.create())
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

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json").build()
            chain.proceed(request)
        }

        return client.build()
    }

    @Singleton
    @Provides
    fun provideWebtoonApi(retrofit: Retrofit) = retrofit.create(WebtoonApi::class.java)
}