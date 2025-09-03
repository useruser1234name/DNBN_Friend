package com.example.dnbn_friend.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import com.example.dnbn_friend.viewmodel.SurveyViewModel

@Composable
fun SubsidySurveyScreen(
    phoneName: String,
    viewModel: SurveyViewModel,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    val answer = viewModel.subsidyAnswer
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "공시지원금 조건 선택", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "선택 기종: $phoneName", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(16.dp))

        ChoiceSection(
            label = "현재 통신사",
            options = listOf("SKT", "KT", "LG U+", "없음"),
            selected = answer.currentCarrier,
            onSelect = { viewModel.updateSubsidyAnswer("currentCarrier", it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ChoiceSection(
            label = "원하는 통신사",
            options = listOf("SKT", "KT", "LG U+", "제한 없음"),
            selected = answer.desiredCarrier,
            onSelect = { viewModel.updateSubsidyAnswer("desiredCarrier", it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ChoiceSection(
            label = "요금제 가격대",
            options = listOf("low", "mid", "high"),
            selected = answer.planTier,
            onSelect = { viewModel.updateSubsidyAnswer("planTier", it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ChoiceSection(
            label = "계약 유형",
            options = listOf("MNP", "DEVICE_CHANGE", "NEW"),
            selected = answer.contractType,
            onSelect = { viewModel.updateSubsidyAnswer("contractType", it) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        val canSubmit = answer.planTier.isNotEmpty() && answer.contractType.isNotEmpty()

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(modifier = Modifier.weight(1f), onClick = onCancel) { Text("취소") }
            Button(modifier = Modifier.weight(1f), onClick = onSubmit, enabled = canSubmit) { Text("결과 보기") }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChoiceSection(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Text(text = label, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))
    Card(colors = CardDefaults.cardColors(), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { opt ->
                    FilterChip(
                        selected = selected == opt,
                        onClick = { onSelect(opt) },
                        label = { Text(opt) }
                    )
                }
            }
        }
    }
}


