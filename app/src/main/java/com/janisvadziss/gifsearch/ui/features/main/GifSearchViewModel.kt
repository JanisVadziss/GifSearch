package com.janisvadziss.gifsearch.ui.features.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.janisvadziss.gifsearch.data.models.GiphyData
import com.janisvadziss.gifsearch.data.repositories.NetworkConnectivityService
import com.janisvadziss.gifsearch.data.repositories.NetworkStatus
import com.janisvadziss.gifsearch.data.repositories.giphy.GiphyRepository
import com.janisvadziss.gifsearch.data.source.CustomResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class GifSearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val giphyRepository: GiphyRepository,
    networkConnectivityService: NetworkConnectivityService
) : ViewModel() {

    private val _events = Channel<GifSearchUiEvent>()
    val events = _events.receiveAsFlow()

    private val _uiState = MutableStateFlow(GifSearchUiState())
    val uiState = _uiState.asStateFlow()

    fun setState(state: GifSearchUiState) {
        _uiState.value = state
    }

    private val _refreshGifsEvent = Channel<Boolean>()
    private val refreshGifsEvent = _refreshGifsEvent
        .receiveAsFlow()
        .onStart { emit(false) }

    private val offset = savedStateHandle.getStateFlow(
        key = GIF_OFFSET_STATE_FLOW,
        initialValue = DEFAULT_OFFSET
    )

    val gifSearchQuery = savedStateHandle.getStateFlow(
        key = GIF_SEARCH_QUERY_STATE_FLOW,
        initialValue = ""
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val GiphyListResponse: StateFlow<List<GiphyData>> = combine(
        refreshGifsEvent,
        gifSearchQuery,
        offset
    ) { _, query, offset ->
        query to offset
    }
    .flatMapLatest { pair ->
        if (pair.second == DEFAULT_OFFSET) {
            clearList()
        }
        giphyRepository.getGifs(
            query = pair.first,
            limit = LIMIT,
            offset = pair.second
        )
    }
    .onEach { result ->
        when (result) {
            is CustomResult.Loading -> _uiState.update { currentUiState ->
                currentUiState.copy(
                    isLoading = true
                )
            }

            is CustomResult.Success -> {
                _uiState.update { currentUiState ->
                    val pagination = result.data.pagination

                    val list = if (pagination.offset == DEFAULT_OFFSET && pagination.totalCount != DEFAULT_OFFSET) {
                        result.data.data
                    } else {
                        (uiState.value.loadedGifs + result.data.data).distinctBy { it.id }
                    }

                    currentUiState.copy(
                        isLoading = false,
                        loadedGifs = list,
                        offset = uiState.value.offset + LIMIT,
                    )
                }
            }

            is CustomResult.Error -> {
                _events.send(
                    GifSearchUiEvent.DisplayMessage(
                        message = result.exception.localizedMessage
                    )
                )
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
    .map { _ ->
         uiState.value.loadedGifs
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    private val networkStatus = networkConnectivityService.networkStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = NetworkStatus.Unknown
        )

    init {
        viewModelScope.launch {
            networkStatus.collect { status ->
                when (status) {
                    is NetworkStatus.Disconnected -> {
                        _events.send(
                            GifSearchUiEvent.DisplayMessage(
                                message = "Network connection is unavailable"
                            )
                        )
                    }

                    else -> { }
                }
            }
        }
    }

    private fun clearList() {
        _uiState.update { currentUiState ->
            currentUiState.copy(
                loadedGifs = listOf(),
                offset = DEFAULT_OFFSET
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[GIF_SEARCH_QUERY_STATE_FLOW] = query
        savedStateHandle[GIF_OFFSET_STATE_FLOW] = DEFAULT_OFFSET
    }

    fun onLoadMoreItems() {
        savedStateHandle[GIF_OFFSET_STATE_FLOW] = uiState.value.offset
    }

    fun onRefresh() {
        viewModelScope.launch {
            if (uiState.value.offset != DEFAULT_OFFSET) {
                savedStateHandle[GIF_OFFSET_STATE_FLOW] = DEFAULT_OFFSET
            }
            _refreshGifsEvent.send(true)
        }
    }

    fun onGifSelected(id: String) {
        viewModelScope.launch {
            _events.send(
                GifSearchUiEvent.NavigateToGifDetails(
                    id = id
                )
            )
        }
    }

    companion object {
        private const val GIF_SEARCH_QUERY_STATE_FLOW = "GIF_SEARCH_QUERY_STATE_FLOW"
        private const val GIF_OFFSET_STATE_FLOW = "GIF_OFFSET_STATE_FLOW"

        const val LIMIT = 25
        const val DEFAULT_OFFSET = 0
    }
}

sealed interface GifSearchUiEvent {
    data class DisplayMessage(
        val message: String?
    ) : GifSearchUiEvent

    data class NavigateToGifDetails(
        val id: String,
    ) : GifSearchUiEvent
}

data class GifSearchUiState(
    val isLoading: Boolean = false,
    val offset: Int = 0,

    val loadedGifs: List<GiphyData> = listOf()
)
