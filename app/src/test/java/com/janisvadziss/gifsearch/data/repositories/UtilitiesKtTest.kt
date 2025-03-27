package com.janisvadziss.gifsearch.data.repositories

import com.google.gson.Gson
import com.janisvadziss.gifsearch.data.models.ErrorResponse
import com.janisvadziss.gifsearch.data.models.Meta
import com.janisvadziss.gifsearch.data.source.CustomResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ExecuteApiTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `executeApi with successful response should emit Success`() = runTest(testScheduler) {
        val expectedData = "Success Data"
        val response = Response.success(expectedData)

        val result = executeApi(testDispatcher) { response }.last()

        Assert.assertEquals(CustomResult.Success(expectedData), result)
    }

    @Test
    fun `executeApi with unsuccessful response should emit Error`() = runTest(testScheduler) {
        val expectedStatus = 400
        val expectedMessage = "Bad Request"
        val errorResponse = ErrorResponse(
            meta = Meta(
                status = expectedStatus,
                msg = expectedMessage,
                responseId = ""
            )
        )

        val errorJson = Gson().toJson(errorResponse)
        val errorBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())

        val response = Response.error<String>(expectedStatus, errorBody)

        val result = executeApi<String>(testDispatcher) { response }.last() as CustomResult.Error
        val expectedErrorMessage = "Status $expectedStatus $expectedMessage"
        assertEquals(CustomResult.Error(Exception(expectedErrorMessage)).exception.message, result.exception.message)
    }
    @Test
    fun `executeApi with unsuccessful response and no errorBody should emit Unknown Error`() = runTest(testScheduler) {
        val expectedStatus = 400
        val response = Response.error<String>(expectedStatus, "".toResponseBody("application/json".toMediaTypeOrNull()))

        val result = executeApi<String>(testDispatcher) { response }.last() as CustomResult.Error
        Assert.assertEquals(CustomResult.Error(Exception("Unknown error")).exception.message, result.exception.message)
    }

    @Test
    fun `executeApi with IO exception should emit Error`() = runTest(testScheduler) {
        val expectedException = IOException("Network error")

        val result = executeApi<String>(testDispatcher) { throw expectedException }.last()
        Assert.assertEquals(CustomResult.Error(expectedException), result)
    }

    @Test
    fun `executeApi should emit Loading before Success`() = runTest(testScheduler) {
        val expectedData = "Success Data"
        val response = Response.success(expectedData)
        val result = executeApi(testDispatcher) { response }.toList()

        Assert.assertEquals(2, result.size)
        Assert.assertEquals(CustomResult.Loading, result[0])
        Assert.assertEquals(CustomResult.Success(expectedData), result[1])
    }

    @Test
    fun `executeApi should emit Loading before Error on response error`() = runTest(testScheduler) {
        val expectedStatus = 400
        val expectedMessage = "Bad Request"
        val errorResponse = ErrorResponse(
            meta = Meta(
                status = expectedStatus,
                msg = expectedMessage,
                responseId = ""
            )
        )

        val errorJson = Gson().toJson(errorResponse)
        val errorBody = errorJson.toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(expectedStatus, errorBody)
        val result = executeApi<String>(testDispatcher) { response }.toList()

        val expectedErrorMessage = "Status $expectedStatus $expectedMessage"
        Assert.assertEquals(2, result.size)
        Assert.assertEquals(CustomResult.Loading, result[0])
        Assert.assertEquals(CustomResult.Error(Exception(expectedErrorMessage)).exception.message, (result[1] as CustomResult.Error).exception.message)
    }

    @Test
    fun `executeApi should emit Loading before Error on IO Exception`() = runTest(testScheduler) {
        val expectedException = IOException("Network error")

        val result = executeApi<String>(testDispatcher) { throw expectedException }.toList()
        Assert.assertEquals(2, result.size)
        Assert.assertEquals(CustomResult.Loading, result[0])
        Assert.assertEquals(CustomResult.Error(expectedException), result[1])
    }
}