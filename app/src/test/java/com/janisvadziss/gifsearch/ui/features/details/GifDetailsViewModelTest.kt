package com.janisvadziss.gifsearch.ui.features.details

import androidx.lifecycle.SavedStateHandle
import com.janisvadziss.gifsearch.data.models.MockData
import com.janisvadziss.gifsearch.data.models.SingleGiphyResponse
import com.janisvadziss.gifsearch.data.repositories.NetworkConnectivityService
import com.janisvadziss.gifsearch.data.repositories.NetworkStatus
import com.janisvadziss.gifsearch.data.repositories.giphy.GiphyRepository
import com.janisvadziss.gifsearch.data.source.CustomResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.delayEach
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class GifDetailsViewModelTest {
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)
    private lateinit var viewModelScope: CoroutineScope
    private lateinit var savedStateHandle: SavedStateHandle

    @Mock
    private lateinit var giphyRepository: GiphyRepository

    private lateinit var networkConnectivityService: NetworkConnectivityService

    private lateinit var viewModel: GifDetailsViewModel
    private lateinit var networkStatusFlow: MutableSharedFlow<NetworkStatus>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModelScope = CoroutineScope(testDispatcher)
        savedStateHandle = SavedStateHandle(mapOf(GIF_ID to "testGifId"))
        networkStatusFlow = MutableSharedFlow()
        networkConnectivityService = object : NetworkConnectivityService {
            override val networkStatus = networkStatusFlow
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        viewModelScope.cancel()
    }

    @Test
    fun gifLoadingEmitsLoading() = runTest(testScheduler) {
        `when`(giphyRepository.getById(any(), any())).thenReturn(flowOf(CustomResult.Loading))

        viewModelScope.launch {
            viewModel = GifDetailsViewModel(savedStateHandle, giphyRepository, networkConnectivityService)
        }
        testScheduler.advanceUntilIdle()

        val result = viewModel.uiState.first()
        assertEquals(GifDetailsUiState(isLoading = true), result)
    }

    @Test
    fun gifSuccessEmitsSuccess() = runTest(testScheduler) {
        val expectedGif = MockData.createMockSingleGiphyResponse()
        `when`(giphyRepository.getById(any(), any())).thenReturn(flowOf(CustomResult.Success(expectedGif)))

        viewModelScope.launch {
            viewModel = GifDetailsViewModel(savedStateHandle, giphyRepository, networkConnectivityService)
        }
        testScheduler.advanceUntilIdle()

        val result = viewModel.uiState.value
        assertEquals(GifDetailsUiState(isLoading = false, gifDetails = expectedGif), result)
    }

    @Test
    fun gifErrorEmitsError() = runTest(testScheduler) {
        val expectedMessage = "test exception"
        `when`(giphyRepository.getById(any(), any())).thenReturn(flowOf(CustomResult.Error(Exception(expectedMessage))))

        viewModelScope.launch {
            viewModel = GifDetailsViewModel(savedStateHandle, giphyRepository, networkConnectivityService)
        }
        testScheduler.advanceUntilIdle()

        val result = viewModel.events.first()
        assertEquals(GifDetailsUiEvent.DisplayMessage(expectedMessage), result)
    }

    @Test
    fun gifErrorEmitsLoadingFalse() = runTest(testScheduler) {
        val expectedMessage = "test exception"
        `when`(giphyRepository.getById(any(), any())).thenReturn(flowOf(CustomResult.Error(Exception(expectedMessage))))

        viewModelScope.launch {
            viewModel = GifDetailsViewModel(savedStateHandle, giphyRepository, networkConnectivityService)
        }
        testScheduler.advanceUntilIdle()

        val result = viewModel.uiState.value
        assertEquals(GifDetailsUiState(isLoading = false), result)
    }
}