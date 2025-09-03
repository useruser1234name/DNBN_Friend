package com.example.dnbn_friend.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dnbn_friend.viewmodel.SurveyViewModel

@Composable
fun SurveyScreen(viewModel: SurveyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProgressIndicator(
            currentStep = viewModel.currentStep,
            totalSteps = 6
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            when (viewModel.currentStep) {
                1 -> BudgetQuestion(viewModel)
                2 -> BrandQuestion(viewModel)
                3 -> PurposeQuestion(viewModel)
                4 -> ScreenSizeQuestion(viewModel)
                5 -> CameraQuestion(viewModel)
                6 -> OsPreferenceQuestion(viewModel)
            }
        }
        
        NavigationButtons(
            currentStep = viewModel.currentStep,
            onPrevious = { viewModel.previousStep() },
            onNext = { viewModel.nextStep() },
            isAnswerSelected = when (viewModel.currentStep) {
                1 -> viewModel.surveyAnswer.budget.isNotEmpty()
                2 -> viewModel.selectedBrands.isNotEmpty() || viewModel.surveyAnswer.brand.isNotEmpty()
                3 -> viewModel.selectedPurposes.isNotEmpty() || viewModel.surveyAnswer.purpose.isNotEmpty()
                4 -> viewModel.surveyAnswer.screenSize.isNotEmpty()
                5 -> viewModel.surveyAnswer.cameraImportance.isNotEmpty()
                6 -> viewModel.osPreference.isNotEmpty()
                else -> false
            }
        )
    }
}

@Composable
fun ProgressIndicator(currentStep: Int, totalSteps: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "질문 $currentStep / $totalSteps",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "${(currentStep * 100 / totalSteps)}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { currentStep.toFloat() / totalSteps },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.LightGray
        )
    }
}

@Composable
fun SelectionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuestionLayout(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        options.forEach { option ->
            SelectionCard(
                text = option,
                isSelected = selectedOption == option,
                onClick = { onOptionSelected(option) }
            )
        }
    }
}

@Composable
fun NavigationButtons(
    currentStep: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isAnswerSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentStep > 1) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("이전", fontSize = 16.sp)
            }
        }
        
        Button(
            onClick = onNext,
            enabled = isAnswerSelected,
            modifier = Modifier
                .then(if (currentStep == 1) Modifier.fillMaxWidth() else Modifier.weight(1f))
                .then(if (currentStep > 1) Modifier.padding(start = 8.dp) else Modifier),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (currentStep == 6) "결과 보기" else "다음",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 질문 컴포넌트들
@Composable
fun BudgetQuestion(viewModel: SurveyViewModel) {
    QuestionLayout(
        title = "예산은 어느 정도로 생각하시나요?",
        options = listOf("50만원 이하", "50-100만원", "100-150만원", "150만원 이상"),
        selectedOption = viewModel.surveyAnswer.budget,
        onOptionSelected = { viewModel.updateAnswer(1, it) }
    )
}

@Composable
fun BrandQuestion(viewModel: SurveyViewModel) {
    Text(
        text = "선호하는 브랜드를 선택하세요 (복수 선택 가능)",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    val brands = listOf("Apple", "Samsung", "Google", "Nothing", "OnePlus", "Xiaomi")
    brands.forEach { brand ->
        SelectionCard(
            text = brand,
            isSelected = brand in viewModel.selectedBrands,
            onClick = { viewModel.toggleBrand(brand) }
        )
    }
}

@Composable
fun PurposeQuestion(viewModel: SurveyViewModel) {
    Text(
        text = "주요 사용 목적을 선택하세요 (복수 선택 가능)",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 24.dp)
    )
    val purposes = listOf("게임", "사진/영상", "영상", "업무/공부", "SNS", "통화/문자", "배터리 지속")
    purposes.forEach { p ->
        SelectionCard(
            text = p,
            isSelected = p in viewModel.selectedPurposes,
            onClick = { viewModel.togglePurpose(p) }
        )
    }
}

@Composable
fun ScreenSizeQuestion(viewModel: SurveyViewModel) {
    QuestionLayout(
        title = "화면 크기와 무게는 어떤 것을 선호하시나요?",
        options = listOf("작고 가벼움", "일반", "큰 화면"),
        selectedOption = viewModel.surveyAnswer.screenSize,
        onOptionSelected = { viewModel.updateAnswer(4, it) }
    )
}

@Composable
fun CameraQuestion(viewModel: SurveyViewModel) {
    QuestionLayout(
        title = "카메라 성능이 얼마나 중요하신가요?",
        options = listOf("매우 중요", "보통", "중요하지 않음"),
        selectedOption = viewModel.surveyAnswer.cameraImportance,
        onOptionSelected = { viewModel.updateAnswer(5, it) }
    )
}

@Composable
fun OsPreferenceQuestion(viewModel: SurveyViewModel) {
    QuestionLayout(
        title = "원하는 OS를 선택하세요",
        options = listOf("상관없음", "iOS", "Android"),
        selectedOption = viewModel.osPreference.ifEmpty { "상관없음" },
        onOptionSelected = { if (it == "상관없음") viewModel.updateOsPreference("") else viewModel.updateOsPreference(it) }
    )
}
