package com.example.dnbn_friend.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.example.dnbn_friend.model.Store

object MapNavigationService {
    
    fun openNaverMap(context: Context, store: Store) {
        val pkgName = context.packageName
        // 네이버: 대중교통 길찾기 (route/public)
        val appUri = Uri.parse(
            "nmap://route/public?dlat=${store.latitude}&dlng=${store.longitude}&dname=${Uri.encode(store.name)}&appname=$pkgName"
        )
        val intent = Intent(Intent.ACTION_VIEW, appUri)
        if (isAppInstalled(context, "com.nhn.android.nmap")) {
            context.startActivity(intent)
        } else {
            val webUrl = "https://map.naver.com/v5/search/${Uri.encode(store.address)}"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
        }
    }
    
    fun openKakaoMap(
        context: Context,
        store: Store,
        userLat: Double? = null,
        userLng: Double? = null
    ) {
        val url = if (userLat != null && userLng != null) {
            // 카카오: 대중교통 길찾기 by=PUBLIC
            "kakaomap://route?sp=$userLat,$userLng&ep=${store.latitude},${store.longitude}&by=PUBLIC"
        } else {
            "kakaomap://route?ep=${store.latitude},${store.longitude}&by=PUBLIC"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        
        if (isAppInstalled(context, "net.daum.android.map")) {
            context.startActivity(intent)
        } else {
            val webUrl = "https://map.kakao.com/link/to/${Uri.encode(store.name)},${store.latitude},${store.longitude}"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
        }
    }
    
    fun openGoogleMap(context: Context, store: Store) {
        // Google: 대중교통은 https URL 스킴이 권장. setPackage로 앱 우선 시도
        val httpsUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${store.latitude},${store.longitude}&travelmode=transit")
        val intent = Intent(Intent.ACTION_VIEW, httpsUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // 앱이 처리 못 하면 일반 VIEW로 폴백(브라우저 또는 다른 지도앱)
            context.startActivity(Intent(Intent.ACTION_VIEW, httpsUri))
        }
    }
    
    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
