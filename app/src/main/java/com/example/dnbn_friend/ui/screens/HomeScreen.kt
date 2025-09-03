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
import com.example.dnbn_friend.viewmodel.AuthViewModel
import com.example.dnbn_friend.viewmodel.SurveyViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    surveyViewModel: SurveyViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
        
        // 추천 시작 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { surveyViewModel.startSurvey() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "스마트폰 추천받기",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "6개의 간단한 질문으로 찾아드려요",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
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
