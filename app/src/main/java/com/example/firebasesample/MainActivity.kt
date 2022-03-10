package com.example.firebasesample

import android.os.Bundle
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
import androidx.navigation.compose.rememberNavController
import com.example.firebasesample.ui.login.LoginBody
import com.example.firebasesample.ui.login.LoginViewModel
import com.example.firebasesample.ui.overview.OverviewBody
import com.example.firebasesample.ui.overview.OverviewViewModel
import com.example.firebasesample.ui.signup.SignUpBody
import com.example.firebasesample.ui.signup.SignUpViewModel
import com.example.firebasesample.ui.theme.FirebaseSampleTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


// Get context in composable to put firebase stuff inside viewmodel?
// https://stackoverflow.com/questions/58743541/how-to-get-context-in-jetpack-compose
const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {

    val loginViewModel by viewModels<LoginViewModel>()
    val signupViewModel by viewModels<SignUpViewModel>()
    val overviewViewModel by viewModels<OverviewViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseSampleApp(
                loginViewModel = loginViewModel,
                signUpViewModel = signupViewModel,
                overviewViewModel = overviewViewModel
            )
        }
    }
}

@Composable
fun FirebaseSampleApp(
    loginViewModel: LoginViewModel,
    signUpViewModel: SignUpViewModel,
    overviewViewModel: OverviewViewModel
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
                overviewViewModel = overviewViewModel
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
    overviewViewModel: OverviewViewModel
) {

    val isLoggedIn = loginViewModel.isLoggedIn

    NavHost(
        navController = navController,
        startDestination = if (loginViewModel.userSignedIn()) SampleScreen.Overview.name else SampleScreen.Login.name, // user signed in already
        modifier = modifier
    ) {

        // TODO: Make user navigate to overview when logging in
        if (isLoggedIn) {
            navController.navigate(SampleScreen.Overview.name)
        }

        composable(SampleScreen.Login.name) {
            LoginBody(
                onClickLogin = loginViewModel::signIn,
                onClickGoogleSignIn = loginViewModel::firebaseAuthWithGoogle,
                onClickSignUp = { navController.navigate(SampleScreen.SignUp.name) },
                validEmail = loginViewModel.validEmail,
                validPassword = loginViewModel.validPassword,
                errorMessage = loginViewModel.errorMessage
            )
        }
        composable(SampleScreen.SignUp.name) {
            SignUpBody(onClickSignUp = signUpViewModel::createAccount)
        }
        composable(SampleScreen.Overview.name) {
            OverviewBody(onClickLogout = {
                loginViewModel.signOut() // sign out of firebase
                navController.navigate(SampleScreen.Login.name)
            })
        }
    }
}

