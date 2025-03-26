package com.janisvadziss.gifsearch.ui.features.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janisvadziss.gifsearch.data.models.SingleGiphyResponse
import com.janisvadziss.gifsearch.data.repositories.NetworkConnectivityService
import com.janisvadziss.gifsearch.data.repositories.NetworkStatus
import com.janisvadziss.gifsearch.data.repositories.giphy.GiphyRepository
import com.janisvadziss.gifsearch.data.source.CustomResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    giphyRepository: GiphyRepository,
    networkConnectivityService: NetworkConnectivityService
) : ViewModel() {
    private val arguments = GifDetailsArgs(savedStateHandle)

    private val _events = Channel<GifDetailsUiEvent>()
    val events = _events.receiveAsFlow()

    private val _uiState = MutableStateFlow(GifDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val networkStatus = networkConnectivityService.networkStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = NetworkStatus.Unknown
        )

    init {
        viewModelScope.launch {
            networkStatus.collect { status ->
                when (status) {
                    is NetworkStatus.Disconnected -> {
                        _events.send(
                            GifDetailsUiEvent.DisplayMessage(
                                message = "Network connection is unavailable"
                            )
                        )
                    }

                    else -> {
                    }
                }
            }
        }
        viewModelScope.launch {
            giphyRepository.getById(
                id = arguments.gifId,
                rating = "g",
            ).collect { gif ->
                when (gif) {
                    is CustomResult.Loading -> _uiState.update { currentUiState ->
                        currentUiState.copy(
                            isLoading = true
                        )
                    }

                    is CustomResult.Success -> _uiState.update { currentUiState ->
                        currentUiState.copy(
                            isLoading = false,
                            gifDetails = gif.data
                        )
                    }

                    is CustomResult.Error -> {
                        _events.send(
                            GifDetailsUiEvent.DisplayMessage(
                                message = gif.exception.localizedMessage
                            )
                        )
                        _uiState.update { currentUiState ->
                            currentUiState.copy(
                                isLoading = false,
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed interface GifDetailsUiEvent {
    data class DisplayMessage(
        val message: String?,
    ) : GifDetailsUiEvent
}

data class GifDetailsUiState(
    val isLoading: Boolean = false,

    val gifDetails: SingleGiphyResponse? = null
)
