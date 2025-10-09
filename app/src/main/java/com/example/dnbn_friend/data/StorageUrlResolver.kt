package com.example.dnbn_friend.data

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

object StorageUrlResolver {
    private val cache = ConcurrentHashMap<String, String>()

    private suspend fun ensureSignedIn() {
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            runCatching { auth.signInAnonymously().await() }
        }
    }

    suspend fun resolve(storage: FirebaseStorage, raw: String): String {
        cache[raw]?.let { return it }
        // Storage SDK 경로를 사용하는 경우, 토큰 미보유로 인한 실패를 방지하기 위해 보장 로그인
        if (!raw.startsWith("http")) {
            ensureSignedIn()
        }
        val url = when {
            raw.startsWith("http") -> raw
            raw.startsWith("gs://") -> storage.getReferenceFromUrl(raw).downloadUrl.await().toString()
            else -> storage.reference.child(raw.trimStart('/')).downloadUrl.await().toString()
        }
        cache[raw] = url
        return url
    }
}


