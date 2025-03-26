package com.janisvadziss.gifsearch.ui

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

//fun LazyStaggeredGridState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@Composable
fun LazyStaggeredGridState.isScrolledToTheEnd(): Boolean {
    val isScrolledToEnd by remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == layoutInfo.totalItemsCount - 1
        }
    }
    return isScrolledToEnd
}