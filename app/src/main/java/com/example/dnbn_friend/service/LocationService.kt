package com.example.dnbn_friend.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult

import kotlin.math.*

class LocationService(private val context: Context) {
    // FusedLocationProviderClient 는 GMS 가용 확인 후 지연 초기화
    
    fun getCurrentLocation(
        onSuccess: (Location) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val isGmsAvailable = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

            val hasFine = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasFine && !hasCoarse) {
                onError(Exception("위치 권한이 필요합니다"))
                return
            }

            if (isGmsAvailable) {
                val fusedLocationClient: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            onSuccess(location)
                        } else {
                            // GMS 는 있지만 최근 위치가 없을 때 폴백
                            fallbackToLocationManager(onSuccess, onError)
                        }
                    }
                    .addOnFailureListener {
                        // GMS 경로 실패 시 폴백
                        fallbackToLocationManager(onSuccess, onError)
                    }
                return
            }

            // GMS 미가용: 즉시 폴백
            fallbackToLocationManager(onSuccess, onError)
        } catch (e: Exception) {
            // 드문 보안/런타임 오류도 폴백 시도 후 보고
            try {
                fallbackToLocationManager(onSuccess, onError)
            } catch (_: Exception) {
                onError(e)
            }
        }
    }

    private fun fallbackToLocationManager(
        onSuccess: (Location) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER
        )
        var lastKnown: Location? = null
        for (provider in providers) {
            try {
                val loc = locationManager.getLastKnownLocation(provider)
                if (loc != null) {
                    if (lastKnown == null || (loc.time > lastKnown!!.time)) {
                        lastKnown = loc
                    }
                }
            } catch (_: SecurityException) {
                // 권한 체크는 선행되므로 무시
            }
        }
        if (lastKnown != null) {
            onSuccess(lastKnown!!)
        } else {
            onError(Exception("위치를 가져올 수 없습니다"))
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
