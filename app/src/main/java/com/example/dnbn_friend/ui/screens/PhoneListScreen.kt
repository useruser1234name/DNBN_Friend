package com.example.dnbn_friend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dnbn_friend.model.Phone
import com.example.dnbn_friend.viewmodel.SurveyViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PhoneListScreen(
    viewModel: SurveyViewModel,
    onSelectPhone: (Phone) -> Unit
) {
    val phones = viewModel.getRecommendedPhones()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "추천 휴대폰", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        if (phones.isEmpty()) {
            EmptyResultCard(
                selectedAnswerText = viewModel.surveyAnswer.toString(),
                onRetry = { viewModel.resetSurvey() }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(phones) { phone ->
                    PhoneListItem(phone = phone, onSelect = {
                        viewModel.selectedPhoneId = phone.id
                        onSelectPhone(phone)
                    })
                }
            }
        }
    }
}

@Composable
private fun PhoneListItem(phone: Phone, onSelect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = phone.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale.KOREA).format(phone.price),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(onClick = onSelect) { Text("선택") }
            }
        }
    }
}


