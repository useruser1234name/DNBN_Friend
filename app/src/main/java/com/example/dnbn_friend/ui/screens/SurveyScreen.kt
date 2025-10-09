@file:OptIn(ExperimentalLayoutApi::class)

package com.example.dnbn_friend.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dnbn_friend.viewmodel.SurveyViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.RangeSlider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle

@Composable
fun SurveyScreen(viewModel: SurveyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProgressIndicator(
            currentStep = viewModel.currentStep,
            totalSteps = 5
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
                2 -> BrandPriorityQuestion(viewModel)
                3 -> PurposeQuestion(viewModel)
                4 -> ScreenSizeQuestion(viewModel)
                5 -> CameraQuestion(viewModel)
            }
        }
        
        NavigationButtons(
            currentStep = viewModel.currentStep,
            onPrevious = { viewModel.previousStep() },
            onNext = { viewModel.nextStep() },
            isAnswerSelected = when (viewModel.currentStep) {
                1 -> viewModel.surveyAnswer.budget.isNotEmpty()
                2 -> viewModel.isBrandValid() || viewModel.selectedBrands.isNotEmpty()
                3 -> viewModel.isPurposeValid()
                4 -> viewModel.isScreenValid()
                5 -> viewModel.isCameraValid()
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
                .height(10.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
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
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bg = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
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
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrandPriorityQuestion(viewModel: SurveyViewModel) {
    Column {
        Text(
            text = "선호 브랜드 우선도(0~100)",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        val brands = listOf("Apple", "Samsung", "Google", "Nothing", "OnePlus", "Xiaomi")
        brands.forEach { brand ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(brand, fontWeight = FontWeight.SemiBold)
                    val v = viewModel.brandPriority[brand] ?: 0
                    Slider(
                        value = v.toFloat(),
                        onValueChange = { viewModel.setBrandScore(brand, it.toInt()) },
                        valueRange = 0f..100f,
                        steps = 100
                    )
                    Text("$v / 100", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PurposeQuestion(viewModel: SurveyViewModel) {
    val p = viewModel.purposePrefs
    Column {
        Text(
            text = "휴대폰 사용 목적 중요도",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        PurposeSlider("게임", p.gaming) { s -> viewModel.updatePurpose { copy(gaming = s) } }
        PurposeSlider("사진 촬영", p.photo) { s -> viewModel.updatePurpose { copy(photo = s) } }
        PurposeSlider("영상 시청(유튜브/OTT)", p.videoStream) { s -> viewModel.updatePurpose { copy(videoStream = s) } }
        PurposeSlider("업무/공부", p.workStudy) { s -> viewModel.updatePurpose { copy(workStudy = s) } }
        PurposeSlider("SNS", p.sns) { s -> viewModel.updatePurpose { copy(sns = s) } }
        PurposeSlider("통화/문자", p.callText) { s -> viewModel.updatePurpose { copy(callText = s) } }
        PurposeSlider("배터리 지속", p.batteryLife) { s -> viewModel.updatePurpose { copy(batteryLife = s) } }
        PurposeSlider("내구성/IP", p.ruggedness) { s -> viewModel.updatePurpose { copy(ruggedness = s) } }
        PurposeSlider("AI 기능", p.aiFeatures) { s -> viewModel.updatePurpose { copy(aiFeatures = s) } }
    }
}

@Composable
private fun PurposeSlider(label: String, value: Int, onChange: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontWeight = FontWeight.SemiBold)
            Text("$value", color = Color.Gray, fontSize = 12.sp)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.toInt()) },
            valueRange = 0f..100f,
            steps = 100
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScreenSizeQuestion(viewModel: SurveyViewModel) {
    val s = viewModel.screenPrefs
    Column {
        Text(
            text = "화면/휴대성 선호",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text("원하는 화면 크기(인치)", fontWeight = FontWeight.SemiBold)
        RangeSlider(
            value = s.minInch..s.maxInch,
            onValueChange = { r -> viewModel.updateScreen { copy(minInch = r.start, maxInch = r.endInclusive) } },
            valueRange = 5.8f..7.2f,
            steps = 14
        )
        Text(String.format("%.1f\" ~ %.1f\"", s.minInch, s.maxInch), color = Color.Gray, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        LabeledSlider("가벼움 중요도", s.weightImportance) { v -> viewModel.updateScreen { copy(weightImportance = v) } }
        LabeledSlider("한손 사용 중요도", s.oneHandUse) { v -> viewModel.updateScreen { copy(oneHandUse = v) } }
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = s.flatDisplay, onClick = { viewModel.updateScreen { copy(flatDisplay = !flatDisplay) } }, label = { Text(if (s.flatDisplay) "플랫 선호" else "엣지 허용") })
            FilterChip(selected = s.highRefresh, onClick = { viewModel.updateScreen { copy(highRefresh = !highRefresh) } }, label = { Text("고주사율 선호") })
            FilterChip(selected = s.pwmSensitive, onClick = { viewModel.updateScreen { copy(pwmSensitive = !pwmSensitive) } }, label = { Text("PWM 민감") })
        }
    }
}

@Composable
private fun LabeledSlider(label: String, value: Int, onChange: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontWeight = FontWeight.SemiBold)
            Text("$value", color = Color.Gray, fontSize = 12.sp)
        }
        Slider(value = value.toFloat(), onValueChange = { onChange(it.toInt()) }, valueRange = 0f..100f, steps = 100)
    }
}

@Composable
fun CameraQuestion(viewModel: SurveyViewModel) {
    val c = viewModel.cameraPrefs
    Column {
        Text(
            text = "카메라 선호",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text("자주 찍는 대상(복수)", fontWeight = FontWeight.SemiBold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SurveyViewModel.CameraSubject.values().forEach { s ->
                val selected = s in c.subjects
                FilterChip(
                    selected = selected,
                    onClick = {
                        viewModel.updateCamera { copy(subjects = if (selected) subjects - s else subjects + s) }
                    },
                    label = { Text(subjectToKo(s)) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        LabeledSlider("줌 중요도", c.zoomImportance) { v -> viewModel.updateCamera { copy(zoomImportance = v) } }
        LabeledSlider("접사 중요도", c.macroImportance) { v -> viewModel.updateCamera { copy(macroImportance = v) } }
        LabeledSlider("인물 중요도", c.portraitImportance) { v -> viewModel.updateCamera { copy(portraitImportance = v) } }
        LabeledSlider("야간 중요도", c.nightImportance) { v -> viewModel.updateCamera { copy(nightImportance = v) } }
        LabeledSlider("AF 추적 중요도", c.afTrackingImportance) { v -> viewModel.updateCamera { copy(afTrackingImportance = v) } }
        LabeledSlider("손떨림 보정 중요도", c.oisEisImportance) { v -> viewModel.updateCamera { copy(oisEisImportance = v) } }
        Spacer(Modifier.height(6.dp))
        Text("원하는 광학 줌(최소)", fontWeight = FontWeight.SemiBold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(2,3,5,10).forEach { x ->
                FilterChip(
                    selected = c.requiredOpticalZoomX == x,
                    onClick = { viewModel.updateCamera { copy(requiredOpticalZoomX = x) } },
                    label = { Text("${x}x 이상") }
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("색감/톤", fontWeight = FontWeight.SemiBold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SurveyViewModel.ColorTone.values().forEach { t ->
                FilterChip(
                    selected = c.colorTone == t,
                    onClick = { viewModel.updateCamera { copy(colorTone = t) } },
                    label = { Text(colorToneToKo(t)) }
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("촬영 스타일", fontWeight = FontWeight.SemiBold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SurveyViewModel.ShootingStyle.values().forEach { st ->
                FilterChip(
                    selected = c.shootingStyle == st,
                    onClick = { viewModel.updateCamera { copy(shootingStyle = st) } },
                    label = { Text(shootingStyleToKo(st)) }
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("동영상", fontWeight = FontWeight.SemiBold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SurveyViewModel.VideoRes.values().forEach { r ->
                FilterChip(
                    selected = c.videoResolution == r,
                    onClick = { viewModel.updateCamera { copy(videoResolution = r) } },
                    label = { Text(videoResToKo(r)) }
                )
            }
            FilterChip(selected = c.hdrVideo, onClick = { viewModel.updateCamera { copy(hdrVideo = !hdrVideo) } }, label = { Text("HDR") })
            FilterChip(selected = c.stabilizationPriority, onClick = { viewModel.updateCamera { copy(stabilizationPriority = !stabilizationPriority) } }, label = { Text("안정화 우선") })
        }
    }
}

private fun subjectToKo(s: SurveyViewModel.CameraSubject) = when (s) {
    SurveyViewModel.CameraSubject.People -> "사람"
    SurveyViewModel.CameraSubject.Kids -> "아이"
    SurveyViewModel.CameraSubject.Pets -> "반려동물"
    SurveyViewModel.CameraSubject.Food -> "음식"
    SurveyViewModel.CameraSubject.Landscape -> "풍경"
    SurveyViewModel.CameraSubject.City -> "도심/건축"
    SurveyViewModel.CameraSubject.NightSky -> "야경/천체"
    SurveyViewModel.CameraSubject.Sports -> "스포츠/동체"
    SurveyViewModel.CameraSubject.Documents -> "문서/필기"
}

private fun colorToneToKo(t: SurveyViewModel.ColorTone) = when (t) {
    SurveyViewModel.ColorTone.Natural -> "자연"
    SurveyViewModel.ColorTone.Vivid -> "선명"
    SurveyViewModel.ColorTone.Contrast -> "대비 강함"
    SurveyViewModel.ColorTone.Warm -> "따뜻함"
    SurveyViewModel.ColorTone.Cool -> "차가움"
}

private fun shootingStyleToKo(s: SurveyViewModel.ShootingStyle) = when (s) {
    SurveyViewModel.ShootingStyle.PointAndShoot -> "간편 자동"
    SurveyViewModel.ShootingStyle.SocialReady -> "SNS 즉시"
    SurveyViewModel.ShootingStyle.ProManualRaw -> "프로/RAW"
}

private fun videoResToKo(r: SurveyViewModel.VideoRes) = when (r) {
    SurveyViewModel.VideoRes.R4K30 -> "4K 30"
    SurveyViewModel.VideoRes.R4K60 -> "4K 60"
    SurveyViewModel.VideoRes.R4K120 -> "4K 120"
    SurveyViewModel.VideoRes.R8K24 -> "8K 24"
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
