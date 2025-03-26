package com.janisvadziss.gifsearch.data.models

data class GiphyListResponse(
    val data: List<GiphyData>,
    val meta: Meta,
    val pagination: Pagination
)

data class SingleGiphyResponse(
    val data: GiphyData,
    val meta: Meta,
    val pagination: Pagination
)