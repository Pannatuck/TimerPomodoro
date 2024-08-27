package dev.pan.presentation.signIn

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dev.pan.timerpomodoro.R
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException


class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth

    //
    suspend fun signIn(): IntentSender? {
        val result = try {
            // calls google method that opens UI for log in
            oneTapClient.beginSignIn(
                // building request for google log in
                buildSignInRequest()
            ).await()
        } catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        // passing our request as Intent to show related UI
        return result?.pendingIntent?.intentSender
    }

    // sign in with google (using all googles data needed for it)
    suspend fun signInWithIntent(intent: Intent): SignInResult {
        // variables below used as google way to authorize user
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        // from here we using googles data on user to use to in way we need in our program
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            // if all good return new result with user data
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName ?: "errorName",
                        profilePictureUrl = photoUrl.toString()
                    )
                },
                errorMessage = null
            )
            // if something wrong return error and empty user
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    // log out from account
    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    // get data if already signed in
    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName ?: "errorName",
            profilePictureUrl = photoUrl.toString()
        )
    }

    // needed to create UI sheet to log in with Google
    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true) // that we can use google log in
                    .setFilterByAuthorizedAccounts(false) // if true, shows only one account you signed with before
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true) // if only one google acc, will use it automatically
            .build()
    }
}