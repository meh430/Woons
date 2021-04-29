package com.mehul.woons.DI

import com.mehul.woons.Constants
import com.mehul.woons.remote.WebtoonApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl(Constants.ENDPOINT).client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

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
    fun provideWebtoonApi(retrofit: Retrofit): WebtoonApi = retrofit.create(WebtoonApi::class.java)
}