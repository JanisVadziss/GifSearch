package com.janisvadziss.gifsearch.ui.features.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val ROUTE_GIF_SEARCH = "ROUTE_GIF_SEARCH"

fun NavController.navigateToGifSearchScreen(
    navOptions: NavOptions? = null
) {
    this.navigate(
        ROUTE_GIF_SEARCH,
        navOptions
    )
}

fun NavGraphBuilder.gifSearch(
    navController: NavController
) {
    composable(
        route = ROUTE_GIF_SEARCH,
    ) {
        GifSearchScreen(navController = navController)
    }
}
