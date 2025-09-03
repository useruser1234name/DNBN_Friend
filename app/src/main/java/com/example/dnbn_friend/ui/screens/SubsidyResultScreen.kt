package com.example.dnbn_friend.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dnbn_friend.model.Store
import com.example.dnbn_friend.viewmodel.LocationViewModel
import com.example.dnbn_friend.viewmodel.SurveyViewModel

@Composable
fun SubsidyResultScreen(
    phoneId: String,
    viewModel: SurveyViewModel,
    locationViewModel: LocationViewModel,
    onNavigateStore: (Store) -> Unit
) {
    val userLocation by locationViewModel.userLocation.collectAsState()
    val nearbyStores by locationViewModel.nearbyStores.collectAsState()
    val locationPermissionGranted by locationViewModel.locationPermissionGranted.collectAsState()

    val desiredCarrier = viewModel.subsidyAnswer.desiredCarrier.ifEmpty { null }
    val planTier = viewModel.subsidyAnswer.planTier.ifEmpty { "high" }
    val contractType = viewModel.subsidyAnswer.contractType.ifEmpty { "MNP" }

    val stores = viewModel.getTopSubsidyStores(
        phoneId = phoneId,
        desiredCarrier = desiredCarrier,
        planTier = planTier,
        contractType = contractType,
        limit = 5
    )
    val finalStores = if (stores.isEmpty()) {
        // 완화: 통신사 무시 → 그다음 contractType만 유지 → 그래도 없으면 기본 인접 매장 3
        val anyCarrier = viewModel.getTopSubsidyStores(
            phoneId = phoneId,
            desiredCarrier = null,
            planTier = planTier,
            contractType = contractType,
            limit = 5
        ).ifEmpty {
            viewModel.getTopSubsidyStores(
                phoneId = phoneId,
                desiredCarrier = null,
                planTier = "high",
                contractType = contractType,
                limit = 5
            )
        }
        if (anyCarrier.isNotEmpty()) anyCarrier else nearbyStores.take(3)
    } else stores

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "보조금 상위 매장 TOP 5", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(finalStores) { store ->
                StoreCard(
                    store = store,
                    showDistance = locationPermissionGranted && store.distance != null,
                    onClick = { onNavigateStore(store) },
                    onNavigateClick = { onNavigateStore(store) }
                )
            }
        }
    }
}


