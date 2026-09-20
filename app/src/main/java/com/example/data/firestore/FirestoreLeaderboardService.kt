package com.example.data.firestore

import android.util.Log
import com.example.data.model.LeaderboardItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

enum class LeaderboardSortBy(val fieldName: String, val label: String) {
    POINTS("points", "Points"),
    KILLS("kills", "Top Kills"),
    BOOYAHS("booyahs", "Booyahs / Wins"),
    MATCHES_PLAYED("matchesPlayed", "Matches Played")
}

data class FirestoreSyncState(
    val isConnected: Boolean = false,
    val isSyncing: Boolean = false,
    val lastSyncTime: String = "Live",
    val errorMessage: String? = null
)

class FirestoreLeaderboardService private constructor() {

    private val tag = "FirestoreLeaderboard"
    private val collectionName = "esports_leaderboard"

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.d(tag, "Firestore not available: ${e.message}")
            null
        }
    }

    /**
     * Streams real-time leaderboard items from Firestore.
     * Falls back gracefully to local/cached default list if Firestore is offline or uninitialized.
     */
    fun observeLeaderboard(
        isGuild: Boolean = false,
        sortBy: LeaderboardSortBy = LeaderboardSortBy.POINTS,
        fallbackData: List<LeaderboardItem>
    ): Flow<Pair<List<LeaderboardItem>, FirestoreSyncState>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            val sorted = sortItems(fallbackData, sortBy)
            trySend(Pair(sorted, FirestoreSyncState(isConnected = false, lastSyncTime = "Offline / Local Cache")))
            awaitClose { }
            return@callbackFlow
        }

        trySend(Pair(sortItems(fallbackData, sortBy), FirestoreSyncState(isConnected = true, isSyncing = true, lastSyncTime = "Connecting to Firestore...")))

        val query = firestore.collection(collectionName)
            .whereEqualTo("isGuild", isGuild)
            .orderBy(sortBy.fieldName, Query.Direction.DESCENDING)
            .limit(50)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(tag, "Snapshot listener error", error)
                val sorted = sortItems(fallbackData, sortBy)
                trySend(Pair(sorted, FirestoreSyncState(isConnected = false, lastSyncTime = "Offline / Cache", errorMessage = error.message)))
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val items = snapshot.documents.mapIndexed { index, doc ->
                    val name = doc.getString("name") ?: "Player"
                    val tagStr = doc.getString("tag") ?: ""
                    val booyahs = doc.getLong("booyahs")?.toInt() ?: 0
                    val kills = doc.getLong("kills")?.toInt() ?: 0
                    val points = doc.getLong("points")?.toInt() ?: 0
                    val matches = doc.getLong("matchesPlayed")?.toInt() ?: 0
                    val kd = doc.getDouble("kdRatio") ?: 0.0
                    val winRate = doc.getString("winRate") ?: "${if (matches > 0) (booyahs * 100 / matches) else 0}%"
                    val tier = doc.getString("tier") ?: "Grandmaster"
                    val guildFlag = doc.getBoolean("isGuild") ?: isGuild
                    val badge = when (index) {
                        0 -> "👑"
                        1 -> "🥈"
                        2 -> "🥉"
                        else -> doc.getString("badge") ?: ""
                    }
                    val uid = doc.getString("userId") ?: doc.id

                    LeaderboardItem(
                        rank = index + 1,
                        name = name,
                        tag = tagStr,
                        booyahs = booyahs,
                        kills = kills,
                        points = points,
                        winRate = winRate,
                        tier = tier,
                        isGuild = guildFlag,
                        badge = badge,
                        matchesPlayed = matches,
                        kdRatio = kd,
                        userId = uid
                    )
                }
                trySend(Pair(items, FirestoreSyncState(isConnected = true, isSyncing = false, lastSyncTime = "Live from Firestore")))
            } else {
                // Empty in Firestore: return fallback sorted
                val sorted = sortItems(fallbackData, sortBy)
                trySend(Pair(sorted, FirestoreSyncState(isConnected = true, isSyncing = false, lastSyncTime = "Firestore Ready (Seeded)")))
            }
        }

        awaitClose { registration.remove() }
    }

    /**
     * Seeds initial leaderboard items to Firestore so documents exist in the cloud collection.
     */
    suspend fun seedLeaderboardToFirestore(items: List<LeaderboardItem>): Boolean {
        val firestore = getFirestore() ?: return false
        return try {
            val batch = firestore.batch()
            items.forEach { item ->
                val docId = if (item.userId.isNotBlank()) item.userId else "user_${item.name.replace(" ", "_")}"
                val docRef = firestore.collection(collectionName).document(docId)
                val data = hashMapOf(
                    "name" to item.name,
                    "tag" to item.tag,
                    "booyahs" to item.booyahs,
                    "kills" to item.kills,
                    "points" to item.points,
                    "winRate" to item.winRate,
                    "tier" to item.tier,
                    "isGuild" to item.isGuild,
                    "badge" to item.badge,
                    "matchesPlayed" to item.matchesPlayed,
                    "kdRatio" to item.kdRatio,
                    "userId" to docId,
                    "updatedAt" to System.currentTimeMillis()
                )
                batch.set(docRef, data)
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to seed Firestore leaderboard: ${e.message}")
            false
        }
    }

    /**
     * Updates/Upserts user match performance score in Firestore.
     */
    suspend fun recordUserMatchPerformance(
        userId: String,
        ign: String,
        guildTag: String,
        kills: Int,
        placement: Int,
        currentPoints: Int
    ): Boolean {
        val firestore = getFirestore() ?: return false
        return try {
            val docRef = firestore.collection(collectionName).document(userId)
            val isWin = placement == 1
            val addedPoints = if (isWin) 100 + (kills * 10) else (placement * 5) + (kills * 10)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                if (snapshot.exists()) {
                    val prevKills = snapshot.getLong("kills")?.toInt() ?: 0
                    val prevPoints = snapshot.getLong("points")?.toInt() ?: currentPoints
                    val prevBooyahs = snapshot.getLong("booyahs")?.toInt() ?: 0
                    val prevMatches = snapshot.getLong("matchesPlayed")?.toInt() ?: 0

                    val newMatches = prevMatches + 1
                    val newKills = prevKills + kills
                    val newBooyahs = if (isWin) prevBooyahs + 1 else prevBooyahs
                    val newPoints = prevPoints + addedPoints
                    val newKd = if (newMatches > 0) String.format("%.2f", newKills.toDouble() / newMatches).toDoubleOrNull() ?: 2.5 else 2.5
                    val newWinRate = "${(newBooyahs * 100 / newMatches)}%"

                    transaction.update(
                        docRef,
                        mapOf(
                            "kills" to newKills,
                            "points" to newPoints,
                            "booyahs" to newBooyahs,
                            "matchesPlayed" to newMatches,
                            "kdRatio" to newKd,
                            "winRate" to newWinRate,
                            "updatedAt" to System.currentTimeMillis()
                        )
                    )
                } else {
                    val newKd = kills.toDouble()
                    val data = hashMapOf(
                        "name" to ign,
                        "tag" to guildTag,
                        "booyahs" to if (isWin) 1 else 0,
                        "kills" to kills,
                        "points" to currentPoints + addedPoints,
                        "winRate" to if (isWin) "100%" else "0%",
                        "tier" to "Diamond I",
                        "isGuild" to false,
                        "badge" to if (isWin) "👑" else "",
                        "matchesPlayed" to 1,
                        "kdRatio" to newKd,
                        "userId" to userId,
                        "updatedAt" to System.currentTimeMillis()
                    )
                    transaction.set(docRef, data)
                }
            }.await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to update match performance: ${e.message}")
            false
        }
    }

    fun sortItems(items: List<LeaderboardItem>, sortBy: LeaderboardSortBy): List<LeaderboardItem> {
        val sorted = when (sortBy) {
            LeaderboardSortBy.POINTS -> items.sortedByDescending { it.points }
            LeaderboardSortBy.KILLS -> items.sortedByDescending { it.kills }
            LeaderboardSortBy.BOOYAHS -> items.sortedByDescending { it.booyahs }
            LeaderboardSortBy.MATCHES_PLAYED -> items.sortedByDescending { it.matchesPlayed }
        }
        return sorted.mapIndexed { index, item ->
            item.copy(
                rank = index + 1,
                badge = when (index) {
                    0 -> "👑"
                    1 -> "🥈"
                    2 -> "🥉"
                    else -> item.badge
                }
            )
        }
    }

    companion object {
        @Volatile
        private var instance: FirestoreLeaderboardService? = null

        fun getInstance(): FirestoreLeaderboardService {
            return instance ?: synchronized(this) {
                instance ?: FirestoreLeaderboardService().also { instance = it }
            }
        }
    }
}
