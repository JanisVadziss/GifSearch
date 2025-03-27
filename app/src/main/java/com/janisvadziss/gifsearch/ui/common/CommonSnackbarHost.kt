package com.janisvadziss.gifsearch.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun CommonSnackbarHost(
    hostState: SnackbarHostState
) {
    SnackbarHost(
        hostState = hostState,
        snackbar = { snackbarData ->
            Snackbar(
                snackbarData = snackbarData,
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    )
}

suspend fun SnackbarHostState.showSnackbar(messageToShow: String) = this.showSnackbar(
    message = messageToShow,
    withDismissAction = true
)
