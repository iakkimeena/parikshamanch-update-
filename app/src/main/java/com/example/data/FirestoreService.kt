package com.example.data

import android.util.Log
import com.example.model.Question
import com.example.model.StudentResult
import com.example.model.TestSeries
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val db = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.e("FirestoreService", "FirebaseFirestore initialization failed: ${e.message}")
        null
    }

    suspend fun saveTestSeries(testSeries: TestSeries) {
        try {
            db?.collection("test_series")?.document(testSeries.id)?.set(testSeries)?.await()
        } catch (e: Exception) {
            Log.w("FirestoreService", "Error saving test series: ${e.message}")
        }
    }

    suspend fun saveStudentResult(result: StudentResult) {
        try {
            db?.collection("student_results")?.document(result.id)?.set(result)?.await()
        } catch (e: Exception) {
            Log.w("FirestoreService", "Error saving student result: ${e.message}")
        }
    }
}
