package com.example.firebasesample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firebasesample.ui.components.OverviewTabRow
import com.example.firebasesample.ui.details.anime.AnimeDetailsBody
import com.example.firebasesample.ui.details.anime.AnimeDetailsViewModel
import com.example.firebasesample.ui.login.LoginBody
import com.example.firebasesample.ui.login.LoginViewModel
import com.example.firebasesample.ui.overview.OverviewBody
import com.example.firebasesample.ui.overview.OverviewViewModel
import com.example.firebasesample.ui.profile.ProfileBody
import com.example.firebasesample.ui.profile.ProfileViewModel
import com.example.firebasesample.ui.signup.SignUpBody
import com.example.firebasesample.ui.signup.SignUpViewModel
import com.example.firebasesample.ui.theme.FirebaseSampleTheme


// Get context in composable to put firebase stuff inside viewmodel?
// https://stackoverflow.com/questions/58743541/how-to-get-context-in-jetpack-compose
const val TAG = "MainActivity"

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    val loginViewModel by viewModels<LoginViewModel>()
    val signupViewModel by viewModels<SignUpViewModel>()
    val overviewViewModel by viewModels<OverviewViewModel>()
    val animeDetailsViewModel by viewModels<AnimeDetailsViewModel>()
    val profileViewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseSampleApp(
                loginViewModel = loginViewModel,
                signUpViewModel = signupViewModel,
                overviewViewModel = overviewViewModel,
                animeDetailsViewModel = animeDetailsViewModel,
                profileViewModel = profileViewModel
            )
        }
    }
}

@Composable
@ExperimentalMaterialApi
fun FirebaseSampleApp(
    loginViewModel: LoginViewModel,
    signUpViewModel: SignUpViewModel,
    overviewViewModel: OverviewViewModel,
    animeDetailsViewModel: AnimeDetailsViewModel,
    profileViewModel: ProfileViewModel
) {
    // TODO: Show bottom nav in some screens
    // https://stackoverflow.com/questions/66837991/hide-top-and-bottom-navigator-on-a-specific-screen-inside-scaffold-jetpack-compo
    FirebaseSampleTheme {
        val navController = rememberNavController()
        val backStackEntry = navController.currentBackStackEntryAsState()
        val bottomNavScreens = listOf(SampleScreen.Overview, SampleScreen.Search, SampleScreen.Profile)
        val currentScreen = SampleScreen.fromRoute(
            backStackEntry.value?.destination?.route
        )
        Scaffold(
            bottomBar = {
                if (currentScreen == SampleScreen.Overview || currentScreen == SampleScreen.Profile) {
                    OverviewTabRow(
                        allScreens = bottomNavScreens,
                        onTabSelected = { screen -> navController.navigate(screen.name) },
                        currentScreen = currentScreen
                    )
                }
            }
        ) { innerPadding ->
            FirebaseSampleNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                loginViewModel = loginViewModel,
                signUpViewModel = signUpViewModel,
                overviewViewModel = overviewViewModel,
                animeDetailsViewModel = animeDetailsViewModel,
                profileViewModel = profileViewModel
            )
        }
    }
}

@Composable
@ExperimentalMaterialApi
fun FirebaseSampleNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    signUpViewModel: SignUpViewModel,
    overviewViewModel: OverviewViewModel,
    animeDetailsViewModel: AnimeDetailsViewModel,
    profileViewModel: ProfileViewModel
) {
    val isLoggedIn = loginViewModel.isLoggedIn

    NavHost(
        navController = navController,
        startDestination = if (loginViewModel.userSignedIn() or isLoggedIn) SampleScreen.Overview.name else SampleScreen.Login.name, // user signed in already
        modifier = modifier
    ) {
        composable(SampleScreen.Login.name) {
            LoginBody(
                onClickLogin = loginViewModel::signIn,
                onClickGoogleSignIn = loginViewModel::firebaseAuthWithGoogle,
                onClickSignUp = { navController.navigate(SampleScreen.SignUp.name) },
                validEmail = loginViewModel.validEmail,
                validPassword = loginViewModel.validPassword,
                errorMessage = loginViewModel.errorMessage,
                loadingState = loginViewModel.status
            )
        }
        composable(SampleScreen.SignUp.name) {
            SignUpBody(onClickSignUp = loginViewModel::signUp)
        }
        composable(SampleScreen.Overview.name) {
            OverviewBody(
                animeData = overviewViewModel.animeData,
                onClickAnime = { anime ->
                    navController.navigate("AnimeDetails/${anime.id}")
                }
            )
        }

        val animeDetailsName = SampleScreen.AnimeDetails.name
        composable(
            route = "$animeDetailsName/{animeId}",
            arguments = listOf(
                navArgument("animeId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val id = entry.arguments?.getString("animeId")
            if (id != null) {
                animeDetailsViewModel.getAnime(id)
            }
            AnimeDetailsBody(
                anime = animeDetailsViewModel.anime,
                onClickFavorite = animeDetailsViewModel::addtoFavorites,
                onClickBack = {
                    navController.navigate(SampleScreen.Overview.name) {
                        popUpTo(SampleScreen.Overview.name) { inclusive = true } // pop off everything up to overview screen
                    }
                }
            )
        }

        composable(SampleScreen.Profile.name) {
            ProfileBody(
                getUserData = profileViewModel::getCurrentUser,
                user = profileViewModel.user,
                onClickAnime = { anime ->
                    navController.navigate("AnimeDetails/${anime.id}")
                },
                onClickLogout = {
                    loginViewModel.signOut() // sign out of firebase
                    navController.navigate(SampleScreen.Login.name)
                }
            )
        }
    }
}

