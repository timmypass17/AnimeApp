package com.example.firebasesample.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.firebasesample.data.models.User
import com.example.firebasesample.ui.overview.KitsuApiStatus
import com.example.firebasesample.utli.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

enum class AuthStatus { LOADING, ERROR, DONE }

class LoginViewModel(val auth: FirebaseAuth = Firebase.auth) : ViewModel() {

    private val TAG = "LoginViewModel"

    var isLoggedIn by mutableStateOf(false)
    var validEmail by mutableStateOf(true)
    var validPassword by mutableStateOf(true)
    var errorMessage by mutableStateOf("")
    var status: AuthStatus by mutableStateOf(AuthStatus.DONE)

    val user: FirebaseUser?
    get() = auth.currentUser
    val db = Firebase.firestore

    fun signIn(email: String, password: String) {
        status = AuthStatus.LOADING
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    isLoggedIn = true
                    validEmail = true
                    validPassword = true
                    status = AuthStatus.DONE
                } else {
                    // If sign in fails, display a message to the user.
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
                    status = AuthStatus.ERROR
                }
            }
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(com.example.firebasesample.TAG, "createUserWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(com.example.firebasesample.TAG, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    isLoggedIn = true
                    validEmail = true
                    validPassword = true

                    // Add user document to firestore
                    val newUser = User(
                        uid = user?.uid.toString(),
                        username = user?.displayName ?: user?.email!!,
                        profileImage = user?.photoUrl.toString(),
                        animeFavorites = mutableMapOf()
                    )
                    val docRef = db.document("users/${user?.uid}")
                    Log.d(TAG, "Doing firebase stuff")

                    // Check if user already in firestore
                    docRef
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                Log.i(TAG, "Document Exists: $documentSnapshot")
                            } else {
                                // Add user to firestore
                                docRef
                                    .set(newUser)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG, "Document has been saved!")
                                    }.addOnFailureListener { e ->
                                        Log.w(TAG, "Document was not saved!", e)
                                    }                            }
                            Log.d(TAG, "Done")
                        }
                } else {
                    // If sign in fails, display a message to the user.
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