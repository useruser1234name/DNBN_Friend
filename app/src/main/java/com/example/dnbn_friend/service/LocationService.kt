package com.example.dnbn_friend.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import kotlin.math.*

class LocationService(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    fun getCurrentLocation(
        onSuccess: (Location) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let { onSuccess(it) }
                            ?: onError(Exception("위치를 가져올 수 없습니다"))
                    }
                    .addOnFailureListener { onError(it) }
            } else {
                onError(Exception("위치 권한이 필요합니다"))
            }
        } catch (e: Exception) {
            onError(e)
        }
    }
    
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val r = 6371 // 지구 반지름 (km)
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).toFloat()
    }
}
