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
import com.example.dnbn_friend.util.JsonCache
import java.io.File
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class AppViewModel(application: Application) : AndroidViewModel(application) {
    // Repository 는 FirebaseApp 초기화 이후 지연 생성
    private var bannerRepository: BannerRepository? = null
    private var todayPhoneRepository: TodayPhoneRepository? = null

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _todayPhones = MutableStateFlow<List<TodayPhone>>(emptyList())
    val todayPhones: StateFlow<List<TodayPhone>> = _todayPhones.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var hasPrefetched = false
    private var prefetchRunning = false

    fun prefetch(force: Boolean = false) {
        if (prefetchRunning) return
        if (hasPrefetched && !force) return
        prefetchRunning = true

        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { _isLoading.value = true }
            val context = getApplication<Application>()
            runCatching { FirebaseApp.initializeApp(context) }
            runCatching {
                if (Firebase.auth.currentUser == null) {
                    Firebase.auth.signInAnonymously().await()
                }
            }
            // 디스크 캐시에서 빠르게 초기 데이터 제공
            val cacheDir = File(context.cacheDir, "data-cache")
            val bannersCache = File(cacheDir, "banners.json")
            val todayCache = File(cacheDir, "todayPhones.json")
            val cachedBanners = JsonCache.readList<Banner>(bannersCache, JsonCache.listType<Banner>())
            val cachedToday = JsonCache.readList<TodayPhone>(todayCache, JsonCache.listType<TodayPhone>())
            if (cachedBanners.isNotEmpty() || cachedToday.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    if (cachedBanners.isNotEmpty()) _banners.value = cachedBanners
                    if (cachedToday.isNotEmpty()) _todayPhones.value = cachedToday
                }
            }
            val repoBanner = bannerRepository ?: BannerRepository().also { bannerRepository = it }
            val repoToday = todayPhoneRepository ?: TodayPhoneRepository().also { todayPhoneRepository = it }
            val bannerDeferred = async { retryWithBackoff { repoBanner.fetchBanners() } ?: emptyList() }
            val todayDeferred = async { retryWithBackoff { repoToday.fetchTodayPhones() } ?: emptyList() }
            val (bannersLoaded, todayLoaded) = awaitAll(bannerDeferred, todayDeferred)
            val bannersList = (bannersLoaded as? List<Banner>) ?: emptyList()
            val todayList = (todayLoaded as? List<TodayPhone>) ?: emptyList()

            withContext(Dispatchers.Main) {
                _banners.value = bannersList
                _todayPhones.value = todayList
                _isLoading.value = false
            }
            prefetchImages(bannersList.map { it.imageUrl } + todayList.map { it.imageUrl })

            // 네트워크 성공 시 디스크 캐시 저장
            if (bannersList.isNotEmpty()) JsonCache.writeList(bannersCache, bannersList)
            if (todayList.isNotEmpty()) JsonCache.writeList(todayCache, todayList)

            if (bannersList.isNotEmpty() || todayList.isNotEmpty()) {
                hasPrefetched = true
            }
            prefetchRunning = false
        }
    }

    private suspend fun prefetchImages(urls: List<String?>) {
        val context = getApplication<Application>()
        val dispatcher = Dispatcher().apply {
            maxRequests = 8
            maxRequestsPerHost = 4
        }
        val okHttpClient = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(7, TimeUnit.SECONDS)
            .writeTimeout(7, TimeUnit.SECONDS)
            .build()

        val imageLoader = ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .build()

        val semaphore = Semaphore(permits = 4)
        coroutineScope {
            urls.filterNotNull().forEach { url ->
                launch(Dispatchers.IO) {
                    semaphore.withPermit {
                        val request = ImageRequest.Builder(context)
                            .data(url)
                            .allowHardware(true)
                            .build()
                        runCatching { imageLoader.execute(request) }
                    }
                }
            }
        }
    }

    private suspend fun <T> retryWithBackoff(
        times: Int = 3,
        initialDelayMs: Long = 300,
        maxDelayMs: Long = 2000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T? {
        var currentDelay = initialDelayMs
        repeat(times - 1) {
            try {
                return block()
            } catch (_: Exception) {
                kotlinx.coroutines.delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
            }
        }
        return runCatching { block() }.getOrNull()
    }
}


