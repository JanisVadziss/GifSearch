package com.janisvadziss.gifsearch.data.repositories

import com.janisvadziss.gifsearch.data.models.MockData
import com.janisvadziss.gifsearch.data.repositories.giphy.DefaultGiphyRepository
import com.janisvadziss.gifsearch.data.source.CustomResult
import com.janisvadziss.gifsearch.data.source.remote.GiphyService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import retrofit2.Response
import kotlin.test.assertEquals

class DefaultGiphyRepositoryTest {

    @Mock
    private lateinit var mockGiphyService: GiphyService

    private lateinit var repository: DefaultGiphyRepository
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher: CoroutineDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = DefaultGiphyRepository(testDispatcher, mockGiphyService)
    }

    @Test
    fun getGifsWithEmptyQueryAndCallGetTrending() = runTest(testScheduler) {
        val expectedResponse = Response.success(MockData.createMockGiphyListResponse())
        `when`(
            mockGiphyService.getTrending(
                limit = any(),
                offset = any(),
                rating = any(),
                bundle = any()
            )
        ).thenReturn(expectedResponse)

        val result = repository.getGifs(query = "", limit = 25, offset = 0).last()

        val response = CustomResult.Success(expectedResponse.body()) as CustomResult<*>

        verify(mockGiphyService).getTrending(limit = 25, offset = 0, rating = "g", bundle = "messaging_non_clips")
        assertEquals(response, result)
    }

    @Test
    fun getGifsWithNonEmptyQueryAndCallGetSearch() = runTest(testScheduler) {
        val expectedResponse = Response.success(MockData.createMockGiphyListResponse())
        `when`(
            mockGiphyService.getSearch(
                query = any(),
                limit = any(),
                offset = any(),
                rating = any(),
                language = any(),
                bundle = any()
            )
        ).thenReturn(expectedResponse)

        val result = repository.getGifs(query = "test", limit = 25, offset = 0).last()

        val response = CustomResult.Success(expectedResponse.body()) as CustomResult<*>

        verify(mockGiphyService).getSearch(query = "test", limit = 25, offset = 0, rating = "g", language = "en", bundle = "messaging_non_clips")
        assertEquals(response, result)
    }

    @Test
    fun callGetById() = runTest(testScheduler) {
        val expectedResponse = MockData.createMockSingleGiphyResponse()
        `when`(mockGiphyService.getById(id = any(), rating = any())).thenReturn(Response.success(expectedResponse))

        val result = repository.getById(id = "testId", rating = "g").last()

        verify(mockGiphyService).getById(id = "testId", rating = "g")
        assertEquals(CustomResult.Success(expectedResponse), result)
    }
}