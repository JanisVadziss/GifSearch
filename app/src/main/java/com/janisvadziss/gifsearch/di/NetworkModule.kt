package com.janisvadziss.gifsearch.di

import android.content.Context
import com.janisvadziss.gifsearch.BuildConfig
import com.janisvadziss.gifsearch.data.repositories.DefaultNetworkConnectivityService
import com.janisvadziss.gifsearch.data.repositories.NetworkConnectivityService
import com.janisvadziss.gifsearch.data.source.remote.GiphyService
import com.janisvadziss.gifsearch.data.source.remote.interceptors.ApiKeyInterceptor
import com.janisvadziss.gifsearch.data.source.remote.interceptors.NetworkConnectivityInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGiphyService(retrofit: Retrofit): GiphyService {
        return retrofit.create(GiphyService::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        networkConnectivityInterceptor: NetworkConnectivityInterceptor,
        apiKeyInterceptor: ApiKeyInterceptor
    ): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(networkConnectivityInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideNetworkConnectivityService(
        @ApplicationContext applicationContext: Context
    ): NetworkConnectivityService {
        return DefaultNetworkConnectivityService(applicationContext)
    }
}