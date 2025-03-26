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

/**
 * Shows or queues to be shown a [Snackbar] at the bottom of the [Scaffold] to which this state
 * is attached and suspends until the snackbar has disappeared.
 *
 * [SnackbarHostState] guarantees to show at most one snackbar at a time. If this function is
 * called while another snackbar is already visible, it will be suspended until this snackbar is
 * shown and subsequently addressed. If the caller is cancelled, the snackbar will be removed
 * from display and/or the queue to be displayed.
 *
 * All of this allows for granular control over the snackbar queue from within:
 *
 * @sample androidx.compose.material3.samples.ScaffoldWithCoroutinesSnackbar
 *
 * To change the Snackbar appearance, change it in 'snackbarHost' on the [Scaffold].
 *
 * @param messageToShow text to be shown in the Snackbar
 *
 * @return [SnackbarResult.ActionPerformed] if option action has been clicked or
 * [SnackbarResult.Dismissed] if snackbar has been dismissed via timeout or by the user
 */
suspend fun SnackbarHostState.showSnackbar(messageToShow: String) = this.showSnackbar(
    message = messageToShow,
    withDismissAction = true
)
