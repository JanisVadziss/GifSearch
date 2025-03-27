package com.janisvadziss.gifsearch.ui.features.main

import androidx.lifecycle.SavedStateHandle
import com.janisvadziss.gifsearch.data.repositories.NetworkConnectivityService
import com.janisvadziss.gifsearch.data.repositories.NetworkStatus
import com.janisvadziss.gifsearch.data.repositories.giphy.GiphyRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class GifSearchViewModelTest {
    private lateinit var viewModel: GifSearchViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var giphyRepository: GiphyRepository
    private lateinit var networkConnectivityService: NetworkConnectivityService

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        savedStateHandle = SavedStateHandle()
        giphyRepository = mock(GiphyRepository::class.java)
        networkConnectivityService = mock(NetworkConnectivityService::class.java)

        val networkStatusFlow = flow {
            emit(NetworkStatus.Connected)
        }
        `when`(networkConnectivityService.networkStatus).thenReturn(networkStatusFlow)
        viewModel = GifSearchViewModel(savedStateHandle, giphyRepository, networkConnectivityService)
    }
    @Test
    fun onSearchQueryChangedUpdateQueryAndResetOffset() {
        val query = "funny cats"
        viewModel.onSearchQueryChanged(query)

        assertEquals(query, savedStateHandle.get<String>("GIF_SEARCH_QUERY_STATE_FLOW"))
        assertEquals(GifSearchViewModel.DEFAULT_OFFSET, savedStateHandle.get<Int>("GIF_OFFSET_STATE_FLOW"))
    }

    @Test
    fun onLoadItemsUpdateOffset() {
        viewModel.onLoadMoreItems()

        assertEquals(GifSearchViewModel.DEFAULT_OFFSET, savedStateHandle.get<Int>("GIF_OFFSET_STATE_FLOW"))
    }

    @Test
    fun onGiftSelectedNavigateToDetailsEvent() = runTest {
        val gifId = "12345"

        viewModel.onGifSelected(gifId)

        val event = viewModel.events.first()
        assertTrue(event is GifSearchUiEvent.NavigateToGifDetails)
        assertEquals(gifId, (event as GifSearchUiEvent.NavigateToGifDetails).id)
    }

    @Test
    fun networkConnectedAndNoDisplayMessage() = runTest {
        val channel = Channel<NetworkStatus>(Channel.BUFFERED)
        `when`(networkConnectivityService.networkStatus).thenReturn(channel.receiveAsFlow())
        channel.send(NetworkStatus.Connected)

        val eventReceived = mutableListOf<GifSearchUiEvent>()
        val job = launch {
            viewModel.events.collect { event ->
                eventReceived.add(event)
            }
        }
        advanceTimeBy(100)
        job.cancel()

        assertTrue(eventReceived.isEmpty())
    }
}