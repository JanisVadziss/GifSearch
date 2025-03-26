package com.janisvadziss.gifsearch.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.janisvadziss.gifsearch.ui.features.details.gifDetails
import com.janisvadziss.gifsearch.ui.features.main.ROUTE_GIF_SEARCH
import com.janisvadziss.gifsearch.ui.features.main.gifSearch

@Composable
fun GifSearchNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ROUTE_GIF_SEARCH) {
        gifSearch(
            navController = navController
        )
        gifDetails(
            navController = navController
        )
    }
}