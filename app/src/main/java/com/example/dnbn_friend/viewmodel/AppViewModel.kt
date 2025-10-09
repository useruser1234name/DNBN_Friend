package com.example.dnbn_friend.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.dnbn_friend.data.BannerRepository
import com.example.dnbn_friend.data.TodayPhoneRepository
import com.example.dnbn_friend.model.Banner
import com.example.dnbn_friend.model.TodayPhone
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth

class AppViewModel(application: Application) : AndroidViewModel(application) {
    // Repository 는 FirebaseApp 초기화 이후 지연 생성
    private var bannerRepository: BannerRepository? = null
    private var todayPhoneRepository: TodayPhoneRepository? = null

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _todayPhones = MutableStateFlow<List<TodayPhone>>(emptyList())
    val todayPhones: StateFlow<List<TodayPhone>> = _todayPhones.asStateFlow()

    private var hasPrefetched = false
    private var prefetchRunning = false

    fun prefetch(force: Boolean = false) {
        if (prefetchRunning) return
        if (hasPrefetched && !force) return
        prefetchRunning = true

        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            runCatching { FirebaseApp.initializeApp(context) }
            runCatching {
                if (Firebase.auth.currentUser == null) {
                    Firebase.auth.signInAnonymously().await()
                }
            }
            val repoBanner = bannerRepository ?: BannerRepository().also { bannerRepository = it }
            val repoToday = todayPhoneRepository ?: TodayPhoneRepository().also { todayPhoneRepository = it }
            val bannerDeferred = async { runCatching { repoBanner.fetchBanners() }.getOrDefault(emptyList()) }
            val todayDeferred = async { runCatching { repoToday.fetchTodayPhones() }.getOrDefault(emptyList()) }
            val (bannersLoaded, todayLoaded) = awaitAll(bannerDeferred, todayDeferred)
            val bannersList = (bannersLoaded as? List<Banner>) ?: emptyList()
            val todayList = (todayLoaded as? List<TodayPhone>) ?: emptyList()

            withContext(Dispatchers.Main) {
                _banners.value = bannersList
                _todayPhones.value = todayList
            }
            prefetchImages(bannersList.map { it.imageUrl } + todayList.map { it.imageUrl })

            if (bannersList.isNotEmpty() || todayList.isNotEmpty()) {
                hasPrefetched = true
            }
            prefetchRunning = false
        }
    }

    private fun prefetchImages(urls: List<String?>) {
        val context = getApplication<Application>()
        val imageLoader = ImageLoader(context)
        urls.filterNotNull().forEach { url ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(true)
                .build()
            imageLoader.enqueue(request)
        }
    }
}


