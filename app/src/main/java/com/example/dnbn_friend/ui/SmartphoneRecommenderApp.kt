package com.example.dnbn_friend.ui

import android.Manifest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dnbn_friend.service.LocationService
import com.example.dnbn_friend.ui.screens.*
import com.example.dnbn_friend.viewmodel.AuthViewModel
import com.example.dnbn_friend.viewmodel.LocationViewModel
import com.example.dnbn_friend.viewmodel.SurveyViewModel

@Composable
fun SmartphoneRecommenderApp(
    authViewModel: AuthViewModel = viewModel(),
    surveyViewModel: SurveyViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModel.provideFactory(LocalContext.current)
    )
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val context = LocalContext.current
    
    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            locationViewModel.updateLocationPermission(true)
            // Get current location
            val locationService = LocationService(context)
            locationService.getCurrentLocation(
                onSuccess = { location ->
                    locationViewModel.updateLocation(location)
                },
                onError = { }
            )
        }
    }
    
    // Request location permission on start
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!isLoggedIn && surveyViewModel.currentStep == 0) {
            LoginScreen(authViewModel, surveyViewModel)
        } else {
            AnimatedContent(
                targetState = surveyViewModel.currentStep,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_animation"
            ) { step ->
                when (step) {
                    0 -> HomeScreen(authViewModel, surveyViewModel)
                    in 1..6 -> SurveyScreen(surveyViewModel)
                    7 -> ResultScreen(surveyViewModel, authViewModel, locationViewModel)
                    else -> HomeScreen(authViewModel, surveyViewModel)
                }
            }
        }
    }
}
