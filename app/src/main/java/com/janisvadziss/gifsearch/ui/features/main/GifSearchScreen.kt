package com.janisvadziss.gifsearch.ui.features.main

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.janisvadziss.gifsearch.R
import com.janisvadziss.gifsearch.data.models.GiphyData
import com.janisvadziss.gifsearch.ui.common.CommonGifImage
import com.janisvadziss.gifsearch.ui.common.CommonScaffold
import com.janisvadziss.gifsearch.ui.common.CommonSnackbarHost
import com.janisvadziss.gifsearch.ui.common.showSnackbar
import com.janisvadziss.gifsearch.ui.features.details.navigateToGifDetails
import com.janisvadziss.gifsearch.ui.features.main.GifSearchViewModel.Companion.DEFAULT_OFFSET
import com.janisvadziss.gifsearch.ui.isScrolledToTheEnd
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GifSearchScreen(
    navController: NavController,
    viewModel: GifSearchViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current

    val pullToRefreshState = rememberPullToRefreshState()
    val gridState = rememberLazyStaggeredGridState()

    val isScrolledToEnd = gridState.isScrolledToTheEnd()
    val gifWidth = configuration.screenWidthDp.dp / 2

    val events = remember(key1 = viewModel.events, key2 = lifecycleOwner) {
        viewModel.events.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val gifs by viewModel.GiphyListResponse.collectAsStateWithLifecycle()
    val searchQuery by viewModel.gifSearchQuery.collectAsStateWithLifecycle()

    val onSearchQueryChanged = remember {
        { query: String ->
            viewModel.onSearchQueryChanged(query)
        }
    }

    val onRefresh = remember {
        {
            viewModel.onRefresh()
        }
    }

    val onLoadMoreItems = remember {
        {
            viewModel.onLoadMoreItems()
        }
    }

    val onGifSelected = remember {
        { id: String ->
            viewModel.onGifSelected(id)
        }
    }

    LaunchedEffect(key1 = events) {
        events.collectLatest { event ->
            when (event) {
                is GifSearchUiEvent.DisplayMessage -> {
                    if (event.message.isNullOrEmpty()) {
                        return@collectLatest
                    }

                    snackbarHostState.showSnackbar(messageToShow = event.message)
                }

                is GifSearchUiEvent.NavigateToGifDetails -> {
                    navController.navigateToGifDetails(
                        gifId = event.id
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = uiState.offset == DEFAULT_OFFSET, key2 = gifs.isNotEmpty()) {
        gridState.scrollToItem(0)
    }

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd) {
            delay(0.5.seconds)
            onLoadMoreItems()
        }
    }

    GifSearchView(
        snackbarHost = {
            CommonSnackbarHost(hostState = snackbarHostState)
        },
        searchField = {
            OutlinedTextField(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                label = {
                    Text(stringResource(R.string.search))
                },
                singleLine = true
            )
        },
        listView = {
            ListView(
                gifs,
                uiState.isLoading,
                gifWidth,
                pullToRefreshState,
                gridState,
                onRefresh,
                onGifSelected,
            )
        }
    )
}

@Composable
private fun GifSearchView(
    snackbarHost: @Composable () -> Unit,
    searchField: @Composable () -> Unit,
    listView: @Composable () -> Unit,
) {

    CommonScaffold(
        snackbarHost = snackbarHost
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            searchField()
            listView()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListView(
    gifs: List<GiphyData>,
    isLoading: Boolean,
    gifWidth: Dp,
    pullToRefreshState: PullToRefreshState,
    gridState: LazyStaggeredGridState,
    onRefresh: () -> Unit,
    onGifClicked: (String) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isLoading,
            onRefresh,
            modifier = Modifier.fillMaxHeight(1f),
            state = pullToRefreshState
        ) {
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxWidth(),
                state = gridState,
                columns = StaggeredGridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp
            ) {
                items(
                    items = gifs,
                    key = { item -> item.id }
                ) { item ->
                    CommonGifImage(
                        modifier = Modifier
                            .width(gifWidth)
                            .clickable(
                                role = Role.Button,
                                onClick = {
                                    onGifClicked(item.id)
                                }
                            ),
                        url = item.images.fixedHeight.url,
                        description = item.title
                    )
                }

                if (gifs.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .height(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
