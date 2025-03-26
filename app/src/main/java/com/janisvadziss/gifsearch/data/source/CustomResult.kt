package com.janisvadziss.gifsearch.data.source

sealed class CustomResult<out T> {
    data object Loading : CustomResult<Nothing>()

    data class Success<T>(val data: T) : CustomResult<T>()

    data class Error(val exception: Throwable) : CustomResult<Nothing>()
}
