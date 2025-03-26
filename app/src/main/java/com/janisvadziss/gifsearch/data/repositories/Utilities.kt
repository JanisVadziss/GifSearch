package com.janisvadziss.gifsearch.data.repositories

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.janisvadziss.gifsearch.data.models.ErrorResponse
import com.janisvadziss.gifsearch.data.source.CustomResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.io.IOException

inline fun <T> executeApi(
    coroutineDispatcher: CoroutineDispatcher,
    crossinline callback: suspend () -> Response<T>
): Flow<CustomResult<T>> {
    return flow {
        emit(CustomResult.Loading)

        try {
            val response = callback()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(CustomResult.Success(body))
            } else {
                val gson = Gson()
                val type = object : TypeToken<ErrorResponse>() {}.type
                val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()?.charStream(), type)

                if (errorResponse != null) {
                    emit(CustomResult.Error(Exception("Status ${errorResponse.meta.status} ${errorResponse.meta.msg}")))
                } else {
                    emit(CustomResult.Error(Exception("Unknown error")))
                }
            }
        } catch (exception: IOException) {
            emit(CustomResult.Error(exception))
        }


    }.flowOn(coroutineDispatcher)
}