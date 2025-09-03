package com.example.dnbn_friend.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dnbn_friend.data.DataRepository
import com.example.dnbn_friend.model.Phone
import com.example.dnbn_friend.model.Store
import com.example.dnbn_friend.service.MapNavigationService
import com.example.dnbn_friend.viewmodel.AuthViewModel
import com.example.dnbn_friend.viewmodel.LocationViewModel
import com.example.dnbn_friend.viewmodel.SurveyViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResultScreen(
    viewModel: SurveyViewModel,
    authViewModel: AuthViewModel,
    locationViewModel: LocationViewModel
) {
    val recommendedPhones = viewModel.getRecommendedPhones()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val userLocation by locationViewModel.userLocation.collectAsState()
    val nearbyStores by locationViewModel.nearbyStores.collectAsState()
    val locationPermissionGranted by locationViewModel.locationPermissionGranted.collectAsState()
    
    var selectedStore by remember { mutableStateOf<Store?>(null) }
    var showMapOptions by remember { mutableStateOf(false) }
    var selectedStoreForMap by remember { mutableStateOf<Store?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "추천 스마트폰 TOP 3",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                TextButton(onClick = { viewModel.showFeedbackDialog = true }) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("피드백")
                }
                TextButton(onClick = { viewModel.resetSurvey() }) {
                    Text("다시 하기")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (recommendedPhones.isEmpty()) {
            EmptyResultCard(
                selectedAnswerText = viewModel.surveyAnswer.toString(),
                onRetry = { viewModel.resetSurvey() }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(recommendedPhones) { phone ->
                    PhoneCard(
                        phone = phone,
                        purchaseMethod = viewModel.surveyAnswer.purchaseMethod,
                        onShopClick = { url ->
                            if (!url.isNullOrEmpty()) {
                                uriHandler.openUri(url)
                            }
                        }
                    )
                }
                
                if (viewModel.surveyAnswer.purchaseMethod == "공시지원금") {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "가까운 매장",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (locationPermissionGranted && userLocation != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "위치 기반 정렬",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    
                    if (!locationPermissionGranted) {
                        item {
                            LocationPermissionCard(
                                onRequestPermission = {
                                    // 위치 권한 재요청 로직
                                }
                            )
                        }
                    }
                    
                    // 선택한 휴대폰별 공시 조건이 있으면 그 기준으로 상위 매장 계산 (기본: 첫 번째 추천 기종)
                    val targetPhoneId = recommendedPhones.firstOrNull()?.id
                    val conditionStores = if (targetPhoneId != null && viewModel.surveyAnswer.purchaseMethod == "공시지원금") {
                        // 현재는 SurveyAnswer에 세부항목이 없으므로 기본값: planTier="high", contractType="MNP", desiredCarrier="제한 없음"
                        viewModel.getTopSubsidyStores(
                            phoneId = targetPhoneId,
                            desiredCarrier = null,
                            planTier = "high",
                            contractType = "MNP",
                            limit = 5
                        )
                    } else emptyList()

                    val storesToShow = when {
                        conditionStores.isNotEmpty() -> conditionStores
                        nearbyStores.isNotEmpty() -> nearbyStores.take(3)
                        else -> DataRepository.stores.take(3)
                    }
                    
                    items(storesToShow) { store ->
                        StoreCard(
                            store = store,
                            showDistance = locationPermissionGranted && store.distance != null,
                            onClick = { selectedStore = store },
                            onNavigateClick = {
                                selectedStoreForMap = store
                                showMapOptions = true
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Feedback Dialog
    if (viewModel.showFeedbackDialog) {
        FeedbackDialog(
            onDismiss = { viewModel.showFeedbackDialog = false },
            onSubmit = { rating, comment, improvements ->
                viewModel.submitFeedback(rating, comment, improvements)
            }
        )
    }
    
    // Store Detail Dialog
    selectedStore?.let { store ->
        StoreDetailDialog(
            store = store,
            onDismiss = { selectedStore = null },
            onNavigate = {
                selectedStoreForMap = store
                showMapOptions = true
                selectedStore = null
            }
        )
    }
    
    // Map Options Dialog
    if (showMapOptions && selectedStoreForMap != null) {
        MapOptionsDialog(
            store = selectedStoreForMap!!,
            onDismiss = { 
                showMapOptions = false
                selectedStoreForMap = null
            },
            onSelectMap = { mapType ->
                when (mapType) {
                    "naver" -> MapNavigationService.openNaverMap(context, selectedStoreForMap!!)
                    "kakao" -> MapNavigationService.openKakaoMap(
                        context,
                        selectedStoreForMap!!,
                        userLocation?.latitude,
                        userLocation?.longitude
                    )
                    "google" -> MapNavigationService.openGoogleMap(context, selectedStoreForMap!!)
                }
                showMapOptions = false
                selectedStoreForMap = null
            }
        )
    }
}

@Composable
fun EmptyResultCard(
    selectedAnswerText: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
            Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "조건에 맞는 제품을 찾을 수 없습니다",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "선택한 조건: $selectedAnswerText",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("다른 조건으로 다시 시도하기")
        }
    }
    }
}

@Composable
fun PhoneCard(
    phone: Phone,
    purchaseMethod: String,
    onShopClick: (String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = phone.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatPrice(phone.price),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    phone.features.forEach { feature ->
                        Text(
                            text = "• $feature",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            if (purchaseMethod == "자급제" && phone.shopUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onShopClick(phone.shopUrl) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !phone.shopUrl.isNullOrEmpty()
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("자사몰에서 구매")
                    }
                    
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("다른 쇼핑몰 비교")
                    }
                }
            }
        }
    }
}

@Composable
fun LocationPermissionCard(onRequestPermission: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "위치 권한이 필요합니다",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "가까운 매장을 찾기 위해 위치 정보가 필요해요",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            TextButton(onClick = onRequestPermission) {
                Text("허용")
            }
        }
    }
}

@Composable
fun StoreCard(
    store: Store,
    showDistance: Boolean,
    onClick: () -> Unit,
    onNavigateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = store.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = store.address,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = store.phone,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                if (showDistance && store.distance != null) {
                    Text(
                        text = String.format("%.1fkm", store.distance),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "공시지원금 ${store.subsidies.size}개",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                IconButton(
                    onClick = onNavigateClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "길찾기",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun MapOptionsDialog(
    store: Store,
    onDismiss: () -> Unit,
    onSelectMap: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "지도 앱 선택",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                MapOptionItem(
                    title = "네이버 지도",
                    subtitle = "대중교통 길찾기",
                    color = Color(0xFF03C75A),
                    onClick = { onSelectMap("naver") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                MapOptionItem(
                    title = "카카오맵",
                    subtitle = "빠른 길찾기",
                    color = Color(0xFFFEE500),
                    onClick = { onSelectMap("kakao") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                MapOptionItem(
                    title = "구글 지도",
                    subtitle = "상세 네비게이션",
                    color = Color(0xFF4285F4),
                    onClick = { onSelectMap("google") }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("취소")
                }
            }
        }
    }
}

@Composable
fun MapOptionItem(
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String, List<String>) -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var selectedImprovements by remember { mutableStateOf(setOf<String>()) }
    
    val improvementOptions = listOf(
        "더 많은 제품 추천",
        "정확도 개선",
        "더 세부적인 필터",
        "가격 정보 개선",
        "UI/UX 개선"
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "추천 결과는 어떠셨나요?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                if (i <= rating) Icons.Default.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (i <= rating) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("의견을 남겨주세요") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "개선이 필요한 부분",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                improvementOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedImprovements = if (option in selectedImprovements) {
                                    selectedImprovements - option
                                } else {
                                    selectedImprovements + option
                                }
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = option in selectedImprovements,
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, fontSize = 14.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSubmit(rating, comment, selectedImprovements.toList())
                            onDismiss()
                        },
                        enabled = rating > 0
                    ) {
                        Text("제출")
                    }
                }
            }
        }
    }
}

@Composable
fun StoreDetailDialog(
    store: Store,
    onDismiss: () -> Unit,
    onNavigate: () -> Unit
) {
    val context = LocalContext.current
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = store.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = store.address,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = store.phone,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "공시지원금 정보",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                store.subsidies.forEach { subsidy ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subsidy.phoneName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = subsidy.carrier,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = formatPrice(subsidy.subsidy),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${store.phone}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("전화하기", fontSize = 14.sp)
                    }
                    Button(
                        onClick = onNavigate,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("길찾기", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

private fun formatPrice(price: Int): String {
    return NumberFormat.getCurrencyInstance(Locale.KOREA).format(price)
}
