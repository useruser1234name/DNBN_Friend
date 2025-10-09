package com.example.dnbn_friend.data

import com.example.dnbn_friend.model.TodayPhone
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class TodayPhoneRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    suspend fun fetchTodayPhones(): List<TodayPhone> {
        val snapshot = firestore.collection("todayPhones").get().await()
        return snapshot.documents.mapNotNull { d ->
            val phoneId = d.getString("phoneId")
            val title = d.getString("title")
            val imageRaw = d.getString("imageUrl")
            val desc = d.getString("description")

            if (phoneId.isNullOrEmpty() || title.isNullOrEmpty() || imageRaw.isNullOrEmpty()) null
            else runCatching {
                val resolvedUrl = StorageUrlResolver.resolve(storage, imageRaw)
                TodayPhone(phoneId, title, resolvedUrl, desc)
            }.getOrNull()
        }
    }
}


