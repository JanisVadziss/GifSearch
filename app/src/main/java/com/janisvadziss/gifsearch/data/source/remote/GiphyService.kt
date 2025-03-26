package com.janisvadziss.gifsearch.data.source.remote

import com.janisvadziss.gifsearch.data.models.GiphyListResponse
import com.janisvadziss.gifsearch.data.models.SingleGiphyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GiphyService {

    @GET("v1/gifs/trending")
    suspend fun getTrending(
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("rating") rating: String?,
        @Query("bundle") bundle: String?
    ): Response<GiphyListResponse>

    @GET("v1/gifs/search")
    suspend fun getSearch(
        @Query("q") query: String,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?,
        @Query("rating") rating: String?,
        @Query("lang") language: String?,
        @Query("bundle") bundle: String?
    ): Response<GiphyListResponse>

    @GET("v1/gifs/{id}")
    suspend fun getById(
        @Path("id") id: String,
        @Query("rating") rating: String?,
    ): Response<SingleGiphyResponse>
}