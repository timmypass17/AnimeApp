package com.example.firebasesample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.firebasesample.ui.login.LoginBody
import com.example.firebasesample.ui.login.LoginViewModel
import com.example.firebasesample.ui.overview.OverviewBody
import com.example.firebasesample.ui.signup.SignUpBody
import com.example.firebasesample.ui.signup.SignUpViewModel
import com.example.firebasesample.ui.theme.FirebaseSampleTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    val loginViewModel by viewModels<LoginViewModel>()
    val signupViewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth
        setContent {
            FirebaseSampleApp(
                loginViewModel = loginViewModel,
                signUpViewModel = signupViewModel,
                createAccount = ::createAccount
            )
        }
    }

    /** When initializing your Activity, check to see if the user is currently signed in. **/
    override fun onStart() {
        super.onStart()
//        val currentUser = auth.currentUser
//        // User logged in
//        if (currentUser != null) {
//            // reload()
//        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }
}

@Composable
fun FirebaseSampleApp(
    loginViewModel: LoginViewModel,
    signUpViewModel: SignUpViewModel,
    createAccount: (email: String, password: String) -> Unit
) {
    FirebaseSampleTheme {
        val navController = rememberNavController()
        Scaffold(
        ) { innerPadding ->
            FirebaseSampleNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                loginViewModel = loginViewModel,
                signUpViewModel = signUpViewModel,
                createAccount = createAccount
            )
        }
    }
}

@Composable
fun FirebaseSampleNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    signUpViewModel: SignUpViewModel,
    createAccount: (email: String, password: String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = SampleScreen.Login.name,
        modifier = modifier
    ) {
        composable(SampleScreen.Login.name) {
            LoginBody(
                onClickLogin = loginViewModel::displayEmailPass,
                onClickSignUp = { navController.navigate(SampleScreen.SignUp.name) }
            )
        }
        composable(SampleScreen.SignUp.name) {
            SignUpBody( onClickSignUp = createAccount )
        }
        composable(SampleScreen.Overview.name) {
            OverviewBody()
        }
    }
}
