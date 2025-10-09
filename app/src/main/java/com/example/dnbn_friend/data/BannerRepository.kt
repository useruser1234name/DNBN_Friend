package com.example.dnbn_friend.data

import com.example.dnbn_friend.model.Banner
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class BannerRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    suspend fun fetchBanners(): List<Banner> {
        val snapshot = firestore.collection("banners").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val imageUrlRaw = doc.getString("imageUrl")
                ?: doc.getString("imageStoragePath")
            val title = doc.getString("title")
            val deeplink = doc.getString("deeplink")
            val id = doc.id

            if (imageUrlRaw.isNullOrEmpty()) {
                null
            } else {
                runCatching {
                    val resolvedUrl = StorageUrlResolver.resolve(storage, imageUrlRaw)
                    Banner(id, resolvedUrl, title, deeplink)
                }.getOrNull()
            }
        }
    }
}


