package com.example.firebasesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.firebasesample.ui.overview.OverviewBody
import com.example.firebasesample.ui.signup.SignUpBody
import com.example.firebasesample.ui.theme.FirebaseSampleTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseSampleApp()
        }
    }
}

@Composable
fun FirebaseSampleApp() {
    FirebaseSampleTheme {
        val allScreens = SampleScreen.values().toList()
        val navController = rememberNavController()
        val backStackEntry = navController.currentBackStackEntryAsState()
        val currentScreen = SampleScreen.fromRoute(
            backStackEntry.value?.destination?.route
        )
        Scaffold(

        ) { innerPadding ->
            FirebaseSampleNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun FirebaseSampleNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SampleScreen.Login.name,
        modifier = modifier
    ) {
        composable(SampleScreen.Login.name) {
            LoginBody(
                onClickLogin = { navController.navigate(SampleScreen.Overview.name) },
                onClickSignUp = { navController.navigate(SampleScreen.SignUp.name) }
            )
        }
        composable(SampleScreen.SignUp.name) {
            SignUpBody()
        }
        composable(SampleScreen.Overview.name) {
            OverviewBody()
        }
    }
}