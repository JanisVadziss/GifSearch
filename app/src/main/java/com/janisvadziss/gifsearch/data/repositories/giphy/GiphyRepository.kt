package com.janisvadziss.gifsearch.data.repositories.giphy

import com.janisvadziss.gifsearch.data.models.GiphyListResponse
import com.janisvadziss.gifsearch.data.models.SingleGiphyResponse
import com.janisvadziss.gifsearch.data.source.CustomResult
import kotlinx.coroutines.flow.Flow

interface GiphyRepository {
    suspend fun getGifs(
        query: String,
        limit: Int?,
        offset: Int?,
    ): Flow<CustomResult<GiphyListResponse>>

    suspend fun getById(
        id: String,
        rating: String?,
    ): Flow<CustomResult<SingleGiphyResponse>>
}