package com.example.couplesnight.repository

import com.example.couplesnight.model.CustomMovie
import com.example.couplesnight.model.Movie
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class MatchRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private val genreMap = mapOf(
        "Action" to 28,
        "Comedy" to 35,
        "Drama" to 18
    )

    private val providerMap = mapOf(
        "Amazon Video" to 10,
        "Netflix" to 8,
        "Hulu" to 15,
        "Apple TV" to 2
    )

    suspend fun setUserReady(
        sessionId: String,
        userId: String,
        genres: String,
        minRating: Double,
        providers: String,
        customMovies: List<CustomMovie>
    ) {
        println("🔥 [setUserReady] userId = $userId, sessionId = $sessionId")

        val userData = hashMapOf(
            "userId" to userId,
            "genres" to genres,
            "minRating" to minRating,
            "providers" to providers,
            "customMovies" to customMovies.map {
                mapOf(
                    "id" to it.id,
                    "title" to it.title,
                    "description" to it.overview,
                    "imagePath" to it.imagePath,
                    "isSelected" to it.isSelected
                )
            }
        )

        firestore
            .collection("sessions")
            .document(sessionId)
            .collection("users")
            .document(userId)
            .set(userData)
            .await()
    }

    suspend fun setSelectedMovies(sessionId: String, userId: String, selectedMovies: List<Movie>) {
        val movieData = selectedMovies.map {
            mapOf(
                "id" to it.id,
                "title" to it.title,
                "description" to it.overview,
                "posterPath" to it.posterPath
            )
        }

        firestore
            .collection("sessions")
            .document(sessionId)
            .collection("users")
            .document(userId)
            .update("selectedMovies", movieData)
            .await()
    }

    suspend fun waitForMovieSelection(sessionId: String): List<Movie> {
        val usersRef = firestore.collection("sessions").document(sessionId).collection("users")

        while (true) {
            val snapshot = usersRef.get().await()
            if (snapshot.size() >= 2) {
                val allSelections = snapshot.mapNotNull { doc ->
                    doc["selectedMovies"] as? List<Map<String, Any>>
                }

                if (allSelections.size == 2) {
                    val movies1 = allSelections[0].mapNotNull { it["id"] as? Int }.toSet()
                    val movies2 = allSelections[1].mapNotNull { it["id"] as? Int }.toSet()
                    val mutualIds = movies1.intersect(movies2)

                    val allMovies = allSelections.flatten().distinctBy { it["id"] }
                    val mutualMovies = allMovies.filter { it["id"] in mutualIds }

                    return mutualMovies.map {
                        Movie(
                            id = it["id"] as Int,
                            title = it["title"] as? String ?: "",
                            overview = it["description"] as? String ?: "",
                            posterPath = it["posterPath"] as? String
                        )
                    }
                }
            }
            delay(1000)
        }
    }

    suspend fun waitForMatch(sessionId: String): Map<String, Any> {
        println("🌀 [waitForMatch] sessionId = $sessionId")

        val usersRef = firestore
            .collection("sessions")
            .document(sessionId)
            .collection("users")

        while (true) {
            val snapshot = usersRef.get().await()
            println("👥 Found \${snapshot.size()} users in session $sessionId")

            if (snapshot.size() >= 2) {
                val combinedGenres = mutableSetOf<Int>()
                val combinedProviders = mutableSetOf<Int>()
                var combinedRating = 0.0
                val seenIds = mutableSetOf<Int>()
                val combinedCustomMovies = mutableListOf<CustomMovie>()

                for (doc in snapshot.documents) {
                    val genres = (doc.getString("genres") ?: "")
                        .split(",")
                        .mapNotNull { genreMap[it.trim()] }

                    val providers = (doc.getString("providers") ?: "")
                        .split(",")
                        .mapNotNull { providerMap[it.trim()] }

                    val minRating = doc.getDouble("minRating") ?: 0.0
                    val customMovies = (doc["customMovies"] as? List<Map<String, Any>>)
                        ?.mapNotNull {
                            val id = (it["id"] as? Long)?.toInt() ?: return@mapNotNull null
                            if (!seenIds.add(id)) return@mapNotNull null

                            val posterPath = it["imagePath"] as? String
                            println("📷 Decoding imagePath: $posterPath")

                            CustomMovie(
                                id = id,
                                title = it["title"] as? String ?: "",
                                overview = it["description"] as? String ?: "",
                                imagePath = posterPath,
                                isSelected = it["isSelected"] as? Boolean ?: false
                            )
                        } ?: emptyList()

                    combinedGenres.addAll(genres)
                    combinedProviders.addAll(providers)
                    combinedRating = maxOf(combinedRating, minRating)
                    combinedCustomMovies.addAll(customMovies)
                }

                println("✅ Matched! Genres: $combinedGenres | Providers: $combinedProviders")

                return mapOf(
                    "genres" to combinedGenres.joinToString(","),
                    "providers" to combinedProviders.joinToString(","),
                    "minRating" to combinedRating,
                    "customMovies" to combinedCustomMovies,
                    "watch_region" to "US"
                )
            }

            delay(1000)
        }
    }
}
