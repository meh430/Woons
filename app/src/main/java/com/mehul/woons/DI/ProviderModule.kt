package com.mehul.woons.DI

import android.app.Application
import com.google.gson.Gson
import com.mehul.woons.Constants
import com.mehul.woons.local.DiscoverCacheConverter
import com.mehul.woons.local.DiscoverCacheDatabase
import com.mehul.woons.local.LibraryDatabase
import com.mehul.woons.remote.WebtoonApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Provides daos, databases, retrofit, gson, services
@Module
class ProviderModule {

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl(Constants.ENDPOINT).client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

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

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
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