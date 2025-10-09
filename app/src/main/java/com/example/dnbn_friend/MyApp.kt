package com.example.dnbn_friend

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase 기본 초기화
        runCatching { FirebaseApp.initializeApp(this) }

        // 개발용 App Check 디버그 프로바이더 활성화 (콘솔에서 API 사용 설정 필요)
        runCatching {
            val appCheck = FirebaseAppCheck.getInstance()
            appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        }

        // 앱 시작 시 익명 인증 선행 (Storage/Firestore 첫 호출 지연 완화)
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val auth = Firebase.auth
                if (auth.currentUser == null) {
                    auth.signInAnonymously().await()
                }
            }
        }
    }
}


