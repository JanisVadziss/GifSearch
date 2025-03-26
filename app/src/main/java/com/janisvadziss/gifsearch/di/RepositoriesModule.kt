package com.janisvadziss.gifsearch.di

import com.janisvadziss.gifsearch.data.repositories.giphy.DefaultGiphyRepository
import com.janisvadziss.gifsearch.data.repositories.giphy.GiphyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
@Suppress("Unused")
interface RepositoriesModule {

    @Binds
    fun bindsGiphyRepository(
        defaultGiphyRepository: DefaultGiphyRepository
    ): GiphyRepository
}
