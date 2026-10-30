package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.model.AuthRole
import com.example.ui.AdminViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseAuthService {
    private const val TAG = "FirebaseAuthService"

    val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = try {
            auth.currentUser
        } catch (_: Exception) {
            null
        }

    fun determineRole(email: String?): AuthRole {
        if (email == null) return AuthRole.STUDENT
        val clean = email.trim().lowercase()
        return if (clean == AdminViewModel.PRIMARY_ADMIN_EMAIL.lowercase()) {
            AuthRole.ADMIN
        } else {
            AuthRole.STUDENT
        }
    }

    fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Sign out error", e)
        }
    }

    suspend fun signInWithGoogle(context: Context, serverClientId: String): Result<FirebaseUser> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                signInWithGoogleIdToken(idToken)
            } else {
                Result.failure(Exception("Google क्रेडेंशियल्स अमान्य हैं। कृपया पुनः प्रयास करें।"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Google साइन इन रद्द कर दिया गया।"))
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error: ${e.message}", e)
            Result.failure(Exception(formatErrorMessage(e)))
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In failed", e)
            Result.failure(Exception(formatErrorMessage(e)))
        }
    }

    suspend fun signInWithGoogleIdToken(idToken: String): Result<FirebaseUser> = suspendCoroutine { continuation ->
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user != null) {
                        continuation.resume(Result.success(user))
                    } else {
                        continuation.resume(Result.failure(Exception("उपयोगकर्ता प्रमाणीकरण विफल रहा।")))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Firebase signInWithCredential error", e)
                    continuation.resume(Result.failure(Exception(formatErrorMessage(e))))
                }
        } catch (e: Exception) {
            continuation.resume(Result.failure(Exception(formatErrorMessage(e))))
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> = suspendCoroutine { continuation ->
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    continuation.resume(Result.success(user))
                } else {
                    continuation.resume(Result.failure(Exception("लॉगिन विफल रहा। कृपया विवरण जांचें।")))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(Exception(formatErrorMessage(exception))))
            }
    }

    suspend fun registerWithEmail(name: String, email: String, password: String): Result<FirebaseUser> = suspendCoroutine { continuation ->
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name.trim())
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener {
                            continuation.resume(Result.success(user))
                        }
                } else {
                    continuation.resume(Result.failure(Exception("पंजीकरण विफल रहा।")))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(Exception(formatErrorMessage(exception))))
            }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = suspendCoroutine { continuation ->
        auth.sendPasswordResetEmail(email.trim())
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(Exception(formatErrorMessage(exception))))
            }
    }

    fun formatErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is FirebaseAuthInvalidUserException -> "इस ईमेल से कोई पंजीकृत खाता नहीं मिला।"
            is FirebaseAuthInvalidCredentialsException -> "अमान्य ईमेल या पासवर्ड। कृपया पुनः जांचें।"
            is FirebaseAuthUserCollisionException -> "यह ईमेल पहले से पंजीकृत है। कृपया लॉगिन करें।"
            is FirebaseAuthWeakPasswordException -> "पासवर्ड बहुत कमजोर है। कम से कम 6 अक्षर होने चाहिए।"
            is FirebaseException -> throwable.localizedMessage ?: "प्रमाणीकरण त्रुटि हुई।"
            else -> throwable.message ?: "प्रमाणीकरण के दौरान अज्ञात त्रुटि उत्पन्न हुई।"
        }
    }
}
