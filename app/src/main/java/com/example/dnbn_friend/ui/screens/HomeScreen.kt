package com.example.dnbn_friend.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import com.example.dnbn_friend.viewmodel.AuthViewModel
import com.example.dnbn_friend.viewmodel.SurveyViewModel
import com.example.dnbn_friend.viewmodel.BannerViewModel
import com.example.dnbn_friend.viewmodel.AppViewModel
import com.example.dnbn_friend.model.Banner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dnbn_friend.ui.components.AutoSlidingCarousel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.runtime.LaunchedEffect
import com.example.dnbn_friend.data.BannerRepository
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    surveyViewModel: SurveyViewModel,
    appViewModel: AppViewModel,
    onOpenPhoneRecommendationSurvey: () -> Unit = {},
    onStartSurvey: () -> Unit = {},
    onBannerClick: (Banner) -> Unit = {}
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "안녕하세요, ${currentUser?.name}님",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "오늘은 어떤 폰을 찾으시나요?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            IconButton(onClick = { authViewModel.logout() }) {
                Icon(Icons.Default.ExitToApp, contentDescription = "로그아웃")
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // 앱 전역 선로딩 결과 사용: 동일 AppViewModel 인스턴스를 공유
        val banners by appViewModel.banners.collectAsState()
        val isLoading by appViewModel.isLoading.collectAsState()
        if (banners.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                AutoSlidingCarousel(
                    itemsCount = banners.size,
                    itemHeight = 360.dp
                ) { index ->
                    val banner = banners[index]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp)
                            .clickable { onBannerClick(banner) }
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(banner.imageUrl)
                                .build(),
                            contentDescription = banner.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp)
                        )
                        // 다크모드 대비 텍스트 가독성 오버레이
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f))
                                    )
                                )
                        )
                        if (!banner.title.isNullOrEmpty()) {
                            Text(
                                text = banner.title ?: "",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        } else if (isLoading) {
            // 캐러셀 로딩 플레이스홀더 (초기 데이터 지연 시 UI 공백 방지)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .background(Color.LightGray.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            // 빈 상태 처리: 배너 없음
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color.LightGray.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("현재 표시할 배너가 없습니다.")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // 추천 시작 카드 (클릭 시 네비게이션으로 설문 진입)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStartSurvey() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "스마트폰 추천받기",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "6개의 간단한 질문으로 최적 기종 추천",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 별도 진행용 휴대폰 추천 설문조사 버튼
        Button(
            onClick = onOpenPhoneRecommendationSurvey,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("휴대폰 추천 설문조사")
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // 사용자 선호도 카드
        if (currentUser?.phonePreferences?.isNotEmpty() == true) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "내 선호도",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        currentUser?.phonePreferences?.forEach { pref ->
                            AssistChip(
                                onClick = { },
                                label = { Text(pref, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }
    }
}
