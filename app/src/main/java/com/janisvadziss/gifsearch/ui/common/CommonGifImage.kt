package com.janisvadziss.gifsearch.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.janisvadziss.gifsearch.R

@Composable
fun CommonGifImage(
    modifier: Modifier,
    url: String,
    description: String
){
    val context = LocalContext.current

    val imageUrl = rememberSaveable(url) {
        url
    }

    val model = remember {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .decoderFactory(GifDecoder.Factory())
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = model,
        contentDescription = description,
        contentScale = ContentScale.FillWidth,
        modifier = modifier,
        placeholder = painterResource(id = R.drawable.round_sync_24),
        error = painterResource(id = R.drawable.round_error_outline_24)
    )
}