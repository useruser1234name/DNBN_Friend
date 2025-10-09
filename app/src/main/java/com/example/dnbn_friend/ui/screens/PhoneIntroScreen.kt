package com.example.dnbn_friend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.dnbn_friend.data.DataRepository
import com.example.dnbn_friend.model.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.dnbn_friend.ui.screens.PurchaseMethodBottomSheet

@Composable
fun PhoneIntroScreen(
    phoneId: String,
    onPurchaseClick: () -> Unit,
    introImageUrl: String? = null,
    introText: String? = null
) {
    val phone: Phone? = DataRepository.phones.find { it.id == phoneId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = phone?.name ?: "선택한 기종", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(introImageUrl ?: phone?.imageUrl)
                    .build(),
                contentDescription = phone?.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(text = introText ?: "해당 기종에 대한 소개가 표시됩니다.")

        Spacer(Modifier.height(24.dp))
        Button(onClick = onPurchaseClick, modifier = Modifier.fillMaxWidth()) {
            Text("구매하러 가기")
        }
    }
}


