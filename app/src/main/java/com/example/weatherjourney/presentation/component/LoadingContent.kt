package com.example.weatherjourney.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LoadingContent(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onRefresh
    )

    Box(modifier.pullRefresh(pullRefreshState)) {
        content()
        PullRefreshIndicator(
            isLoading,
            pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.Transparent
        )
    }
}
