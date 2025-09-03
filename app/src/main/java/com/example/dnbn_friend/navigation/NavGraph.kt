package com.example.dnbn_friend.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Survey : Screen("survey")
    object Result : Screen("result")
    object PhoneList : Screen("phone_list")
    object PurchaseMethod : Screen("purchase_method/{phoneId}") {
        fun createRoute(phoneId: String) = "purchase_method/$phoneId"
    }
    object SubsidySurvey : Screen("subsidy_survey/{phoneId}") {
        fun createRoute(phoneId: String) = "subsidy_survey/$phoneId"
    }
    object SubsidyResult : Screen("subsidy_result/{phoneId}") {
        fun createRoute(phoneId: String) = "subsidy_result/$phoneId"
    }
    object StoreDetail : Screen("store_detail/{storeId}") {
        fun createRoute(storeId: String) = "store_detail/$storeId"
    }
}

object NavArguments {
    const val STORE_ID = "storeId"
}
