package com.janisvadziss.gifsearch.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.janisvadziss.gifsearch.R
import com.janisvadziss.gifsearch.ui.theme.Typography

@Composable
fun CommonTitleAndDescription(
    @StringRes title: Int,
    info: String = "",
    imageUrl: String? = null
) {
    val context = LocalContext.current

    val model = remember {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .decoderFactory(GifDecoder.Factory())
            .crossfade(true)
            .build()
    }

    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = model,
                contentDescription = info,
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(start = 16.dp).size(48.dp),
                placeholder = painterResource(id = R.drawable.round_sync_24),
                error = painterResource(id = R.drawable.round_error_outline_24)
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = title),
                style = Typography.titleSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = info.ifEmpty {
                    stringResource(R.string.unknown)
                },
            )
        }
    }

}
