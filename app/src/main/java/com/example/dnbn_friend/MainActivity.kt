package com.example.dnbn_friend

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dnbn_friend.data.DataRepository
import com.example.dnbn_friend.navigation.NavArguments
import com.example.dnbn_friend.navigation.Screen
import com.example.dnbn_friend.model.Phone
import com.example.dnbn_friend.ui.screens.HomeScreen
import com.example.dnbn_friend.ui.screens.LoginScreen
import com.example.dnbn_friend.ui.screens.SubsidySurveyScreen
import com.example.dnbn_friend.ui.screens.ResultScreen
import com.example.dnbn_friend.ui.screens.SurveyScreen
import com.example.dnbn_friend.ui.screens.StoreDetailScreen
import com.example.dnbn_friend.ui.screens.SubsidyResultScreen
import com.example.dnbn_friend.ui.screens.PhoneListScreen
import com.example.dnbn_friend.ui.screens.PurchaseMethodBottomSheet
import com.example.dnbn_friend.ui.theme.SmartphoneRecommenderTheme
import com.example.dnbn_friend.viewmodel.AuthViewModel
import com.example.dnbn_friend.viewmodel.LocationViewModel
import com.example.dnbn_friend.viewmodel.SurveyViewModel
import com.example.dnbn_friend.service.ConsultationService
import com.example.dnbn_friend.model.ConsultationRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartphoneRecommenderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val surveyViewModel: SurveyViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModel.provideFactory(LocalContext.current)
    )

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            val isLoggedIn = authViewModel.isLoggedIn.collectAsState().value
            LoginScreen(
                authViewModel = authViewModel,
                surveyViewModel = surveyViewModel
            )
            androidx.compose.runtime.LaunchedEffect(isLoggedIn) {
                if (isLoggedIn) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.Home.route) {
            val currentStep = surveyViewModel.currentStep
            HomeScreen(
                authViewModel = authViewModel,
                surveyViewModel = surveyViewModel
            )
            androidx.compose.runtime.LaunchedEffect(currentStep) {
                if (currentStep in 1..6) {
                    navController.navigate(Screen.Survey.route)
                } else if (currentStep == 7) {
                    navController.navigate(Screen.PhoneList.route)
                }
            }
        }

        composable(Screen.Survey.route) {
            val currentStep = surveyViewModel.currentStep
            SurveyScreen(viewModel = surveyViewModel)
            androidx.compose.runtime.LaunchedEffect(currentStep) {
                if (currentStep == 7) {
                    navController.navigate(Screen.PhoneList.route) {
                        popUpTo(Screen.Survey.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.PhoneList.route) {
            val context = LocalContext.current
            var selectedPhone by remember { mutableStateOf<Phone?>(null) }
            var showSheet by remember { mutableStateOf(false) }
            PhoneListScreen(
                viewModel = surveyViewModel,
                onSelectPhone = { phone ->
                    selectedPhone = phone
                    showSheet = true
                }
            )
            if (showSheet && selectedPhone != null) {
                val phone = selectedPhone!!
                PurchaseMethodBottomSheet(
                    onDismiss = { showSheet = false },
                    onSelectSimFree = {
                        val url = phone.shopUrl
                        if (!url.isNullOrEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                        showSheet = false
                    },
                    onSelectSubsidy = {
                        navController.navigate(Screen.SubsidySurvey.createRoute(phone.id))
                        showSheet = false
                    }
                )
            }
        }

        // 공시설문: 선택 기종은 간단히 첫 번째 추천 기종 기준으로 연결
        composable(Screen.SubsidySurvey.route) { backStackEntry ->
            val phoneId = backStackEntry.arguments?.getString("phoneId")
            val phoneName = DataRepository.phones.find { it.id == phoneId }?.name ?: "선택 기종"
            SubsidySurveyScreen(
                phoneName = phoneName,
                viewModel = surveyViewModel,
                onSubmit = {
                    navController.navigate(Screen.SubsidyResult.createRoute(phoneId ?: ""))
                },
                onCancel = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.StoreDetail.route,
            arguments = listOf(
                navArgument(NavArguments.STORE_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val storeId = backStackEntry.arguments?.getString(NavArguments.STORE_ID)
            val store = DataRepository.stores.find { it.id == storeId }
            store?.let {
                StoreDetailScreen(
                    store = it,
                    onNavigateUp = { navController.navigateUp() },
                    onConsult = { req ->
                        val enriched = req.copy(
                            phoneId = surveyViewModel.selectedPhoneId,
                            initialSurvey = surveyViewModel.surveyAnswer,
                            subsidySurvey = surveyViewModel.subsidyAnswer
                        )
                        ConsultationService.submitViaEmail(
                            context = context,
                            toEmail = "store@example.com",
                            request = enriched
                        )
                    }
                )
            }
        }

        composable(
            route = Screen.SubsidyResult.route,
            arguments = listOf(
                navArgument("phoneId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneId = backStackEntry.arguments?.getString("phoneId") ?: ""
            SubsidyResultScreen(
                phoneId = phoneId,
                viewModel = surveyViewModel,
                locationViewModel = locationViewModel,
                onNavigateStore = { store ->
                    // 결과 리스트에서 매장 카드 클릭 시 상세/길찾기 공용 다이얼로그를 재사용하려면 ResultScreen 과 유사하게 구현 가능
                    // 일단 상세 화면으로 이동
                    navController.navigate(Screen.StoreDetail.createRoute(store.id))
                }
            )
        }
    }
}