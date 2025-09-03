package com.example.dnbn_friend.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Store(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val subsidies: List<PhoneSubsidy>,
    var distance: Float? = null
) : Parcelable

@Parcelize
data class PhoneSubsidy(
    val phoneId: String,
    val phoneName: String,
    val subsidy: Int,
    val carrier: String,
    val planTier: String, // e.g., "low" | "mid" | "high"
    val contractType: String // e.g., "MNP" | "DEVICE_CHANGE" | "NEW"
) : Parcelable