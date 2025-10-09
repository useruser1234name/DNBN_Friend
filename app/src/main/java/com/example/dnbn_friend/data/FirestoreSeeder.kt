package com.example.dnbn_friend.data

import com.example.dnbn_friend.model.Phone
import com.example.dnbn_friend.model.Store
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreSeeder {
    suspend fun seedAll(db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
        if (isCollectionEmpty(db, "phones")) {
            seedPhones(db)
        }
        if (isCollectionEmpty(db, "stores")) {
            seedStores(db)
        }
    }

    private suspend fun isCollectionEmpty(db: FirebaseFirestore, name: String): Boolean {
        return try {
            db.collection(name).limit(1).get().await().isEmpty
        } catch (_: Exception) {
            true
        }
    }

    private suspend fun seedPhones(db: FirebaseFirestore) {
        val batch = db.batch()
        DataRepository.phones.forEach { phone: Phone ->
            val ref = db.collection("phones").document(phone.id)
            val data = hashMapOf(
                "id" to phone.id,
                "name" to phone.name,
                "brand" to phone.brand,
                "price" to phone.price,
                "imageUrl" to phone.imageUrl,
                "features" to phone.features,
                "screenSize" to phone.screenSize,
                "cameraScore" to phone.cameraScore,
                "purposes" to phone.purposes,
                "shopUrl" to phone.shopUrl
            )
            batch.set(ref, data)
        }
        batch.commit().await()
    }

    private suspend fun seedStores(db: FirebaseFirestore) {
        val batch = db.batch()
        DataRepository.stores.forEach { store: Store ->
            val ref = db.collection("stores").document(store.id)
            val data = hashMapOf(
                "id" to store.id,
                "name" to store.name,
                "address" to store.address,
                "phone" to store.phone,
                "latitude" to store.latitude,
                "longitude" to store.longitude,
                "subsidies" to store.subsidies.map { s ->
                    mapOf(
                        "phoneId" to s.phoneId,
                        "phoneName" to s.phoneName,
                        "subsidy" to s.subsidy,
                        "carrier" to s.carrier,
                        "planTier" to s.planTier,
                        "contractType" to s.contractType
                    )
                }
            )
            batch.set(ref, data)
        }
        batch.commit().await()
    }
}


