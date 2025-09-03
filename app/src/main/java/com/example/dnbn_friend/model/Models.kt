package com.example.dnbn_friend.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val profileImage: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val phonePreferences: List<String> = emptyList(),
    val surveyHistory: List<SurveyResult> = emptyList()
)

data class Phone(
    val id: String,
    val name: String,
    val brand: String,
    val price: Int,
    val imageUrl: String,
    val features: List<String>,
    val screenSize: String,
    val cameraScore: Int,
    val purposes: List<String>,
    val shopUrl: String? = null
)

data class SurveyAnswer(
    val budget: String = "",
    val brand: String = "",
    val purpose: String = "",
    val screenSize: String = "",
    val cameraImportance: String = "",
    val purchaseMethod: String = ""
)

data class SubsidySurveyAnswer(
    val currentCarrier: String = "",   // 현재 통신사
    val desiredCarrier: String = "",   // 원하는 통신사 (또는 "제한 없음")
    val planTier: String = "",         // low | mid | high
    val contractType: String = ""      // MNP | DEVICE_CHANGE | NEW
)

data class ConsultationRequest(
    val userId: String,
    val storeId: String,
    val phoneId: String?,
    val initialSurvey: SurveyAnswer,
    val subsidySurvey: SubsidySurveyAnswer,
    val timestamp: Long = System.currentTimeMillis(),
    val appVersion: String? = null,
    val userLocation: UserLocation? = null
)

data class SurveyResult(
    val id: String,
    val userId: String,
    val answers: SurveyAnswer,
    val recommendedPhones: List<String>,
    val feedback: Feedback? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class Feedback(
    val rating: Int,
    val comment: String,
    val improvements: List<String> = emptyList()
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)