package com.example.dnbn_friend.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dnbn_friend.model.Store
import com.example.dnbn_friend.model.ConsultationRequest
import com.example.dnbn_friend.model.SubsidySurveyAnswer
import com.example.dnbn_friend.model.SurveyAnswer
import com.example.dnbn_friend.service.ConsultationService
import com.example.dnbn_friend.service.MapNavigationService
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    store: Store,
    onNavigateUp: () -> Unit,
    onConsult: (ConsultationRequest) -> Unit = {}
) {
    val context = LocalContext.current
    var showMapOptions by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("매장 상세 정보") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .padding(bottom = 24.dp)
        ) {
            // 매장 정보
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = store.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 공시지원금 정보
            Text(
                text = "공시지원금 정보",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
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
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 하단 버튼
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
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("전화하기")
                }
                
                Button(
                    onClick = { showMapOptions = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("길찾기")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val req = ConsultationRequest(
                        userId = com.example.dnbn_friend.data.DataRepository.currentUser?.id ?: "guest",
                        storeId = store.id,
                        phoneId = null,
                        initialSurvey = SurveyAnswer(),
                        subsidySurvey = SubsidySurveyAnswer()
                    )
                    onConsult(req)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("상담신청 하기")
            }
        }
    }
    
    if (showMapOptions) {
        AlertDialog(
            onDismissRequest = { showMapOptions = false },
            title = { Text("지도 앱 선택") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            MapNavigationService.openNaverMap(context, store)
                            showMapOptions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("네이버 지도")
                    }
                    TextButton(
                        onClick = {
                            MapNavigationService.openKakaoMap(context, store)
                            showMapOptions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("카카오맵")
                    }
                    TextButton(
                        onClick = {
                            MapNavigationService.openGoogleMap(context, store)
                            showMapOptions = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("구글 지도")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMapOptions = false }) {
                    Text("취소")
                }
            }
        )
    }
}

private fun formatPrice(price: Int): String {
    return NumberFormat.getCurrencyInstance(Locale.KOREA).format(price)
}
