package com.example.dnbn_friend.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseMethodBottomSheet(
    onDismiss: () -> Unit,
    onSelectSimFree: () -> Unit,
    onSelectSubsidy: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("구매 방식을 선택하세요")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onSelectSimFree, modifier = Modifier.fillMaxWidth()) { Text("자급제") }
            Button(onClick = onSelectSubsidy, modifier = Modifier.fillMaxWidth()) { Text("공시지원금") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("닫기") }
        }
    }
}


