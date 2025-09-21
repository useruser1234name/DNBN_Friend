package com.example.dnbn_friend.viewmodel

import android.content.Context
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dnbn_friend.data.DataRepository
import com.example.dnbn_friend.model.*
import com.example.dnbn_friend.service.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationViewModel(private val context: Context) : ViewModel() {
    private val locationService = LocationService(context)
    
    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation.asStateFlow()
    
    private val _nearbyStores = MutableStateFlow<List<Store>>(emptyList())
    val nearbyStores: StateFlow<List<Store>> = _nearbyStores.asStateFlow()
    
    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted.asStateFlow()
    
    fun updateLocation(location: Location) {
        _userLocation.value = UserLocation(
            latitude = location.latitude,
            longitude = location.longitude
        )
        updateNearbyStores(location.latitude, location.longitude)
    }
    
    fun updateLocationPermission(granted: Boolean) {
        _locationPermissionGranted.value = granted
    }
    
    private fun updateNearbyStores(userLat: Double, userLon: Double) {
        val storesWithDistance = DataRepository.stores.map { store ->
            store.copy(
                distance = locationService.calculateDistance(
                    userLat, userLon,
                    store.latitude, store.longitude
                )
            )
        }.sortedBy { it.distance }
        
        _nearbyStores.value = storesWithDistance
    }
    
    fun getNearbyStores(limit: Int = 5): List<Store> {
        return _nearbyStores.value.take(limit)
    }
    
    companion object {
        fun provideFactory(
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LocationViewModel(context) as T
            }
        }
    }
}

class AuthViewModel : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    fun loginWithSocial(provider: String) {
        _isLoggedIn.value = true
        _currentUser.value = DataRepository.currentUser
    }
    
    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
    }
    
    fun updateUserProfile(age: Int?, gender: String?, preferences: List<String>) {
        _currentUser.value = _currentUser.value?.copy(
            age = age,
            gender = gender,
            phonePreferences = preferences
        )
        DataRepository.currentUser = _currentUser.value
    }
}

class SurveyViewModel : ViewModel() {
    var currentStep by mutableStateOf(0)
        private set
    
    var surveyAnswer by mutableStateOf(SurveyAnswer())
        private set
    
    var currentSurveyResult by mutableStateOf<SurveyResult?>(null)
        private set
    
    var showFeedbackDialog by mutableStateOf(false)

    // 확장 상태: 멀티 선택 및 OS 선호
    var selectedBrands by mutableStateOf(setOf<String>())
        private set
    var selectedPurposes by mutableStateOf(setOf<String>())
        private set
    var osPreference by mutableStateOf("")

    // 공시설문 상태
    var subsidyAnswer by mutableStateOf(SubsidySurveyAnswer())
        private set

    // 사용자가 PhoneList에서 고른 기종
    var selectedPhoneId by mutableStateOf<String?>(null)
    
    fun updateAnswer(step: Int, answer: String) {
        surveyAnswer = when (step) {
            1 -> surveyAnswer.copy(budget = answer)
            2 -> surveyAnswer.copy(brand = answer)
            3 -> surveyAnswer.copy(purpose = answer)
            4 -> surveyAnswer.copy(screenSize = answer)
            5 -> surveyAnswer.copy(cameraImportance = answer)
            6 -> surveyAnswer
            else -> surveyAnswer
        }
    }

    fun toggleBrand(brand: String) {
        selectedBrands = if (brand in selectedBrands) selectedBrands - brand else selectedBrands + brand
    }

    fun togglePurpose(purpose: String) {
        selectedPurposes = if (purpose in selectedPurposes) selectedPurposes - purpose else selectedPurposes + purpose
    }

    fun updateOsPreference(os: String) {
        osPreference = os
    }

    fun updateSubsidyAnswer(field: String, value: String) {
        subsidyAnswer = when (field) {
            "currentCarrier" -> subsidyAnswer.copy(currentCarrier = value)
            "desiredCarrier" -> subsidyAnswer.copy(desiredCarrier = value)
            "planTier" -> subsidyAnswer.copy(planTier = value)
            "contractType" -> subsidyAnswer.copy(contractType = value)
            else -> subsidyAnswer
        }
    }
    
    fun startSurvey() {
        currentStep = 1
    }
    
    fun nextStep() {
        if (currentStep < 6) {
            currentStep++
        } else if (currentStep == 6) {
            saveSurveyResult()
            currentStep = 7
        }
    }
    
    fun previousStep() {
        if (currentStep > 1) {
            currentStep--
        }
    }
    
    fun resetSurvey() {
        currentStep = 0
        surveyAnswer = SurveyAnswer()
        currentSurveyResult = null
    }
    
    private fun saveSurveyResult() {
        val recommendedPhones = getRecommendedPhones()
        currentSurveyResult = SurveyResult(
            id = System.currentTimeMillis().toString(),
            userId = DataRepository.currentUser?.id ?: "",
            answers = surveyAnswer,
            recommendedPhones = recommendedPhones.map { it.id }
        )
    }
    
    fun submitFeedback(rating: Int, comment: String, improvements: List<String>) {
        currentSurveyResult = currentSurveyResult?.copy(
            feedback = Feedback(rating, comment, improvements)
        )
        showFeedbackDialog = false
    }
    
    fun getRecommendedPhones(): List<Phone> {
        var filteredPhones = DataRepository.phones
        
        println("Initial phones: ${filteredPhones.size}")
        println("Survey answers: $surveyAnswer")
        
        // OS 선호 필터 우선 적용
        filteredPhones = when (osPreference) {
            "iOS" -> filteredPhones.filter { it.brand == "Apple" }
            "Android" -> filteredPhones.filter { it.brand != "Apple" }
            else -> filteredPhones
        }

        filteredPhones = when (surveyAnswer.budget) {
            "50만원 이하" -> filteredPhones.filter { it.price <= 500000 }
            "50-100만원" -> filteredPhones.filter { it.price in 500000..1000000 }
            "100-150만원" -> filteredPhones.filter { it.price in 1000000..1500000 }
            "150만원 이상" -> filteredPhones.filter { it.price >= 1500000 }
            else -> filteredPhones
        }
        println("After budget filter: ${filteredPhones.size}")
        
        filteredPhones = when {
            selectedBrands.isNotEmpty() -> filteredPhones.filter { it.brand in selectedBrands }
            surveyAnswer.brand != "상관없음" && surveyAnswer.brand.isNotEmpty() -> filteredPhones.filter { it.brand == surveyAnswer.brand }
            else -> filteredPhones
        }
        println("After brand filter: ${filteredPhones.size}")
        
        if (surveyAnswer.screenSize.isNotEmpty()) {
            filteredPhones = filteredPhones.filter { it.screenSize == surveyAnswer.screenSize }
            println("After screen size filter: ${filteredPhones.size}")
        }
        
        filteredPhones = when (surveyAnswer.cameraImportance) {
            "매우 중요" -> filteredPhones.filter { it.cameraScore >= 4 }
            "보통" -> filteredPhones.filter { it.cameraScore >= 3 }
            else -> filteredPhones
        }
        println("After camera filter: ${filteredPhones.size}")
        
        filteredPhones = when {
            selectedPurposes.isNotEmpty() -> filteredPhones.filter { phone -> phone.purposes.any { it in selectedPurposes } }
            surveyAnswer.purpose.isNotEmpty() -> filteredPhones.filter { it.purposes.contains(surveyAnswer.purpose) }
            else -> filteredPhones
        }
        println("After purpose filter: ${filteredPhones.size}")
        
        val result = filteredPhones.take(3)
        println("Final recommended phones: ${result.map { it.name }}")
        return result
    }

    // 공시지원금 상위 매장 랭킹 (복구)
    fun getTopSubsidyStores(
        phoneId: String,
        desiredCarrier: String?,
        planTier: String,
        contractType: String,
        limit: Int = 5
    ): List<Store> {
        val carrierFilter: (String) -> Boolean = { c ->
            desiredCarrier.isNullOrEmpty() || desiredCarrier == "제한 없음" || desiredCarrier == c
        }

        val scored = DataRepository.stores.mapNotNull { store ->
            val best = store.subsidies
                .asSequence()
                .filter { it.phoneId == phoneId }
                .filter { carrierFilter(it.carrier) }
                .filter { it.planTier == planTier }
                .filter { it.contractType == contractType }
                .maxByOrNull { it.subsidy }
            best?.let { bestSubsidy -> store to bestSubsidy.subsidy }
        }

        return scored
            .sortedByDescending { it.second }
            .map { it.first }
            .take(limit)
    }
}

class BannerViewModel : ViewModel() {
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    fun load(repository: com.example.dnbn_friend.data.BannerRepository) {
        viewModelScope.launch {
            runCatching { repository.fetchBanners() }
                .onSuccess { list -> _banners.value = list }
                .onFailure { e ->
                    println("Banner load error: ${e.message}")
                    _banners.value = emptyList()
                }
        }
    }
}

class TodayPhoneViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<TodayPhone>>(emptyList())
    val items: StateFlow<List<TodayPhone>> = _items.asStateFlow()

    fun load(repo: com.example.dnbn_friend.data.TodayPhoneRepository) {
        viewModelScope.launch {
            runCatching { repo.fetchTodayPhones() }
                .onSuccess { list -> _items.value = list }
                .onFailure { _items.value = emptyList() }
        }
    }
}