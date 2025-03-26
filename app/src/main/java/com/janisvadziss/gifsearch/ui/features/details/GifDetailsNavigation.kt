package com.janisvadziss.gifsearch.ui.features.details

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val ROUTE_GIF_DETAILS = "ROUTE_GIF_DETAILS"
const val GIF_ID = "GIF_ID"

class GifDetailsArgs(savedStateHandle: SavedStateHandle) {

    private val _gifId: String = checkNotNull(savedStateHandle[GIF_ID]) as String
    val gifId: String
        get() = _gifId
}

fun NavController.navigateToGifDetails(
    gifId: String,
    navOptions: NavOptions? = null
) {
    this.navigate(
        "$ROUTE_GIF_DETAILS/$gifId",
        navOptions
    )
}

fun NavGraphBuilder.gifDetails(
    navController: NavController
) {
    composable(
        route = "$ROUTE_GIF_DETAILS/{$GIF_ID}",
        arguments = listOf(
            navArgument(GIF_ID) {
                nullable = true
                type = NavType.StringType
            }
        )
    ) {
        GifDetailsScreen(navController = navController)
    }
}
