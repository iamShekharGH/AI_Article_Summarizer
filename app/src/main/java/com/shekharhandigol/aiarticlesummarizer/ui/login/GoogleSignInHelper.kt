package com.shekharhandigol.aiarticlesummarizer.ui.login

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.shekharhandigol.aiarticlesummarizer.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Helper class for Google Sign-In using Credential Manager and Firebase Authentication.
 * Based on the implementation found at: https://github.com/himanshuGaur684/Google_Sign_in-Credential_Manager/blob/main/app/src/main/java/gaur/himanshu/login/GoogleSignInUtils.kt
 */
class GoogleSignInHelper {
    companion object {

        fun isUserLoggedIn(): Boolean {
            val firebaseUser = Firebase.auth.currentUser
            return firebaseUser != null && !firebaseUser.isAnonymous
        }

        fun doGoogleSignIn(
            context: Context,
            scope: CoroutineScope,
            activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
            onSuccess: () -> Unit,
            onError: (Exception, String) -> Unit
        ) {
            // Check if user is already logged in
            if (isUserLoggedIn()) {
                onSuccess()
                return
            }
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                // .setNonce(YOUR_NONCE)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(context, request)
                    val credential = result.credential

                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        handleGoogleIdTokenCredential(credential, onSuccess, onError)
                    } else {
                        onError(
                            IllegalStateException("Unexpected credential type."),
                            "Sign-in failed. Unexpected credential type."
                        )
                    }
                } catch (e: NoCredentialException) {
                    e.printStackTrace()
                    activityResultLauncher?.launch(createAddAccountIntent())
                } catch (e: GetCredentialException) {
                    e.printStackTrace()
                    onError(e, "Sign-in failed. Could not retrieve credentials.")
                } catch (e: Exception) {
                    e.printStackTrace()
                    onError(e, "An unexpected error occurred during sign-in.")
                }
            }
        }

        private suspend fun handleGoogleIdTokenCredential(
            customCredential: CustomCredential,
            onSuccess: () -> Unit,
            onError: (Exception, String) -> Unit
        ) {
            try {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(customCredential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                if (googleIdToken.isBlank()) {
                    onError(
                        IllegalStateException("Google ID Token is empty."),
                        "Sign-in failed. Invalid token received."
                    )
                    return
                }

                val firebaseAuthCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = Firebase.auth.signInWithCredential(firebaseAuthCredential).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null && !firebaseUser.isAnonymous) {
                    onSuccess()
                } else {
                    onError(
                        IllegalStateException("Firebase user is null or anonymous."),
                        "Sign-in completed, but user data is not valid."
                    )
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                e.printStackTrace()
                onError(
                    e,
                    "An account already exists with the same email address but different sign-in credentials."
                )
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e, "Sign-in failed while processing your Google account.")
            }
        }

        private fun createAddAccountIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }
    }
}