package com.example.firebasesample.ui.login

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasesample.R
import com.example.firebasesample.utli.Auth
import com.example.firebasesample.utli.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class LoginViewModel(val auth: FirebaseAuth = Firebase.auth) : ViewModel() {

    private val TAG = "LoginViewModel"

    var isLoggedIn by mutableStateOf(false)

    var validEmail by mutableStateOf(true)
    var validPassword by mutableStateOf(true)

    var errorMessage by mutableStateOf("")

    val user: FirebaseUser?
    get() = auth.currentUser

    fun signIn(email: String, password: String) {
        Log.i(TAG, "email: \"$email\", password: \"$password\" ")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")

                    isLoggedIn = true
                    validEmail = true
                    validPassword = true
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Log.i(TAG, task.exception?.message!!)
                    isLoggedIn = false

                    // Get firebase error message
                    val exception = task.exception?.message
                    // Update error message to display to user
                    errorMessage = Auth.getError(exception)
                    // Update email, pass state
                    when (exception) {
                        Auth.EmailBad.message -> validEmail = false
                        Auth.PasswordBad.message -> validPassword = false
                        else -> { validEmail = false; validPassword = false }
                    }
                }
            }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    isLoggedIn = true
                    validEmail = true
                    validPassword = true
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    isLoggedIn = false

                    // Get firebase error message
                    val exception = task.exception?.message
                    // Update error message to display to user
                    errorMessage = Auth.getError(exception)
                    // Update email, pass state
                    when (exception) {
                        Auth.EmailBad.message -> validEmail = false
                        Auth.PasswordBad.message -> validPassword = false
                        else -> { validEmail = false; validPassword = false }
                    }
                }
            }
    }


    fun signOut() {
        Firebase.auth.signOut()
        isLoggedIn = false // reset
    }

    fun userSignedIn(): Boolean {
        return user != null
    }
}