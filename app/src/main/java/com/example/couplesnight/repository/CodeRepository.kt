package com.example.couplesnight.repository

import android.content.Context
import com.example.couplesnight.database.AppDatabase
import com.example.couplesnight.model.Code
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.tasks.await

class CodeRepository(context: Context) {

    private val codeDao = AppDatabase.getDatabase(context).codeDao()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)  // Disable local persistence
            .build()
        firestore.firestoreSettings = settings
    }

    suspend fun saveCodeLocally(code: Code) {
        codeDao.insert(code)
    }

    suspend fun saveCodeRemotely(code: String) {
        firestore.collection("codes")
            .document(code)
            .set(mapOf("code" to code))
            .await()
    }

    suspend fun validateCodeRemotely(inputCode: String): Boolean {
        val snapshot = firestore.collection("codes")
            .document(inputCode)
            .get()
            .await()
        return snapshot.exists()
    }
}