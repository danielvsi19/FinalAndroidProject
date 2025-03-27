package com.example.couplesnight.data.repository

// data/repository/UserRepository.kt
import com.example.couplesnight.data.local.UserDao
import com.example.couplesnight.data.local.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val authRepo: AuthRepository,
    private val userDao: UserDao
) {
    // Unified Login
    suspend fun login(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val success = authRepo.login(email, password)
            if (success) cacheCurrentUser()
            success
        }
    }

    // Unified Sign-Up
    suspend fun signUp(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val success = authRepo.signUp(email, password)
            if (success) cacheCurrentUser()
            success
        }
    }

    // Cache Firebase user in Room
    private suspend fun cacheCurrentUser() {
        val firebaseUser = authRepo.getCurrentUser() ?: return
        val user = User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = firebaseUser.displayName ?: "User",
            profilePicUrl = firebaseUser.photoUrl?.toString()
        )
        userDao.insertUser(user)
    }

    // Get cached user (for offline access)
    suspend fun getCachedUser(uid: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUser(uid)
        }
    }

    // Google Sign-In
    suspend fun signInWithGoogle(idToken: String): Boolean {
        return withContext(Dispatchers.IO) {
            val success = authRepo.signInWithGoogle(idToken)
            if (success) cacheCurrentUser()
            success
        }
    }
}