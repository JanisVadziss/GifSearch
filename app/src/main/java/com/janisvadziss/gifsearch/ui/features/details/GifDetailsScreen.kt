package com.janisvadziss.gifsearch.ui.features.details

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.janisvadziss.gifsearch.R
import com.janisvadziss.gifsearch.data.models.GiphyData
import com.janisvadziss.gifsearch.ui.common.CommonGifImage
import com.janisvadziss.gifsearch.ui.common.CommonScaffold
import com.janisvadziss.gifsearch.ui.common.CommonSnackbarHost
import com.janisvadziss.gifsearch.ui.common.CommonTitleAndDescription
import com.janisvadziss.gifsearch.ui.common.showSnackbar
import com.janisvadziss.gifsearch.ui.features.main.GifSearchUiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GifDetailsScreen(
    navController: NavController,
    viewModel: GifDetailsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    val events = remember(key1 = viewModel.events, key2 = lifecycleOwner) {
        viewModel.events.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = events) {
        events.collectLatest { event ->
            Log.d("crates_events", event.toString())

            when (event) {
                is GifDetailsUiEvent.DisplayMessage -> {
                    if (event.message.isNullOrEmpty()) {
                        return@collectLatest
                    }

                    snackbarHostState.showSnackbar(messageToShow = event.message)
                }
            }
        }
    }

    GifDetails(
        navController = navController,
        gif = uiState.gifDetails?.data,
        snackbarHost = {
            CommonSnackbarHost(hostState = snackbarHostState)
        },
        isLoading = uiState.isLoading,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GifDetails(
    navController: NavController,
    gif: GiphyData?,
    snackbarHost: @Composable () -> Unit,
    isLoading: Boolean,
) {

    CommonScaffold(
        snackbarHost = snackbarHost,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.details),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            if (isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            if (gif == null) {
                return@CommonScaffold
            }

            val context = LocalContext.current

            val imageUrl = rememberSaveable(gif.images.original.url) {
                gif.images.original.url
            }

            val model = remember {
                ImageRequest.Builder(context)
                    .data(imageUrl)
                    .decoderFactory(GifDecoder.Factory())
                    .crossfade(true)
                    .build()
            }

            CommonTitleAndDescription(
                title = R.string.title,
                info = gif.title
            )

            SubcomposeAsyncImage(
                model = model,
                contentDescription = gif.title,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(),
                loading = {
                    CircularProgressIndicator()
                },
                error = {
                    Image(
                        painterResource(id = R.drawable.round_error_outline_24),
                        contentDescription = gif.title
                    )
                }
            )

            CommonTitleAndDescription(
                title = R.string.created_by,
                info = gif.user?.displayName.orEmpty(),
                imageUrl = gif.user?.avatarUrl,
            )

            if (!gif.user?.description.isNullOrBlank()){
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = gif.user?.description.orEmpty(),
                )
            }

            Spacer(
                modifier = Modifier.height(56.dp)
            )
        }
    }
}