package com.janisvadziss.gifsearch.data.repositories.giphy

import com.janisvadziss.gifsearch.data.models.GiphyListResponse
import com.janisvadziss.gifsearch.data.models.SingleGiphyResponse
import com.janisvadziss.gifsearch.data.repositories.executeApi
import com.janisvadziss.gifsearch.data.source.CustomResult
import com.janisvadziss.gifsearch.data.source.remote.GiphyService
import com.janisvadziss.gifsearch.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class DefaultGiphyRepository @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val giphyService: GiphyService
) : GiphyRepository {

    override suspend fun getGifs(
        query: String,
        limit: Int?,
        offset: Int?,
    ): Flow<CustomResult<GiphyListResponse>> {
        return executeApi(
            coroutineDispatcher = coroutineDispatcher,
        ) {
            if (query.isEmpty()) {
                giphyService.getTrending(
                    limit = limit,
                    offset = offset,
                    rating = "g",
                    bundle = "messaging_non_clips"
                )
            } else {
                delay(1.seconds)
                giphyService.getSearch(
                    query = query,
                    limit = limit,
                    offset = offset,
                    rating = "g",
                    language = "en",
                    bundle = "messaging_non_clips"
                )
            }
        }
    }

    override suspend fun getById(id: String, rating: String?): Flow<CustomResult<SingleGiphyResponse>> {
        return executeApi(
            coroutineDispatcher = coroutineDispatcher,
        ) {
            giphyService.getById(
                id = id,
                rating = rating
            )
        }
    }
}