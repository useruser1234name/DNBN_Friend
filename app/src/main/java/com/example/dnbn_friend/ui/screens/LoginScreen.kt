package com.example.dnbn_friend.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dnbn_friend.viewmodel.AuthViewModel
import com.example.dnbn_friend.viewmodel.SurveyViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    surveyViewModel: SurveyViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "스마트폰 추천 서비스",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "나에게 맞는 스마트폰을 찾아보세요",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        SocialLoginButton(
            text = "Google로 시작하기",
            backgroundColor = Color.White,
            textColor = Color.Black,
            borderColor = Color.LightGray,
            onClick = { 
                authViewModel.loginWithSocial("google")
                surveyViewModel.resetSurvey()
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SocialLoginButton(
            text = "Kakao로 시작하기",
            backgroundColor = Color(0xFFFEE500),
            textColor = Color(0xFF3C1E1E),
            onClick = { 
                authViewModel.loginWithSocial("kakao")
                surveyViewModel.resetSurvey()
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SocialLoginButton(
            text = "Naver로 시작하기",
            backgroundColor = Color(0xFF03C75A),
            textColor = Color.White,
            onClick = { 
                authViewModel.loginWithSocial("naver")
                surveyViewModel.resetSurvey()
            }
        )
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    borderColor: Color? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .then(
                if (borderColor != null) 
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(8.dp))
                else Modifier
            ),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
