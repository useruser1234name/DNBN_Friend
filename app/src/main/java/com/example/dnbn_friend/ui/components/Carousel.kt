package com.example.dnbn_friend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import kotlinx.coroutines.delay

@Composable
fun IndicatorDot(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unSelectedColor: Color = Color.LightGray,
    dotSize: Dp = 8.dp
) {
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        items((0 until totalDots).toList()) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor else unSelectedColor,
                size = dotSize
            )
            if (index != totalDots - 1) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier,
    autoSlideDuration: Long = 3000L,
    pagerState: PagerState = rememberPagerState(),
    itemsCount: Int,
    itemHeight: Dp = 180.dp,
    itemContent: @Composable (index: Int) -> Unit,
) {
    // 간단 자동 슬라이딩: 일정 주기로 다음 페이지로 이동 (루프 기반, 중복 애니메이션 방지)
    LaunchedEffect(itemsCount) {
        if (itemsCount <= 0) return@LaunchedEffect
        while (true) {
            delay(autoSlideDuration)
            if (!pagerState.isScrollInProgress) {
                val next = (pagerState.currentPage + 1) % itemsCount
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Box(modifier = modifier
        .fillMaxWidth()
        .clipToBounds()
    ) {
        HorizontalPager(
            count = itemsCount,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .clipToBounds()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                itemContent(page)
            }
        }

        Surface(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.BottomCenter),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.3f)
        ) {
            DotsIndicator(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                totalDots = itemsCount,
                selectedIndex = pagerState.currentPage
            )
        }
    }
}


