package com.example.firebasesample

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
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
import com.example.firebasesample.ui.search.SearchScreen
import com.example.firebasesample.ui.search.SearchViewModel
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
    val searchViewModel by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseSampleApp(
                loginViewModel = loginViewModel,
                signUpViewModel = signupViewModel,
                overviewViewModel = overviewViewModel,
                animeDetailsViewModel = animeDetailsViewModel,
                profileViewModel = profileViewModel,
                searchViewModel = searchViewModel
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
    profileViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel
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
                if (currentScreen != SampleScreen.Login || currentScreen != SampleScreen.SignUp) {
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
                profileViewModel = profileViewModel,
                searchViewModel = searchViewModel
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
    profileViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel
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
                overviewStatus = overviewViewModel.overviewStatus,
                animeData = overviewViewModel.animeData,
                onClickAnime = { anime ->
                    // When user clicks on poster, fetch "One Piece" data and navigate to details
                    animeDetailsViewModel.getAnime(anime.node.id)
                    animeDetailsViewModel.getUserRatings(anime.node.id)
                    profileViewModel.getCurrentUser()
                    navController.navigate("AnimeDetails/${anime.node.id}")
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
            AnimeDetailsBody(
                anime = animeDetailsViewModel.anime,
                user = profileViewModel.user,
                isFavorited = animeDetailsViewModel.isFavorited,
                isWatched = animeDetailsViewModel.isWatched,
                onClickFavorite = animeDetailsViewModel::addtoFavorites,
                onClickRemoveFavorite = animeDetailsViewModel::removeFromFavorites,
                onClickWatched = animeDetailsViewModel::addToWatched,
                onClickRemoveWatched = animeDetailsViewModel::removeFromWatched,
                status = animeDetailsViewModel.status,
                onClickBack = {
                    navController.navigate(SampleScreen.Overview.name) {
                        popUpTo(SampleScreen.Overview.name) { inclusive = true } // pop off screen until
                    }
                },
                onClickAddReview = animeDetailsViewModel::addReview,
                userReviews = animeDetailsViewModel.reviews,
                onClickRelatedAnime = { relatedAnime ->
                    // When user clicks on poster, fetch "One Piece" data and navigate to details
                    animeDetailsViewModel.getAnime(relatedAnime.node.id)
                    animeDetailsViewModel.getUserRatings(relatedAnime.node.id)
                    profileViewModel.getCurrentUser()
                    navController.navigate("AnimeDetails/${relatedAnime.node.id}") {
                        popUpTo(SampleScreen.Overview.name) { inclusive = false }
                    }
                },
                onClickRecommended = { animeRecommendation ->
                    // When user clicks on poster, fetch "One Piece" data and navigate to details
                    animeDetailsViewModel.getAnime(animeRecommendation.node.id)
                    animeDetailsViewModel.getUserRatings(animeRecommendation.node.id)
                    profileViewModel.getCurrentUser()
                    navController.navigate("AnimeDetails/${animeRecommendation.node.id}")
                },
                onClickDeleteComment = animeDetailsViewModel::deleteComment
            )
        }

        composable(SampleScreen.Profile.name) {
            // When user presses profile screen, get fresh user data
            profileViewModel.getCurrentUser()
            Log.i(TAG, "Getting user data")

            ProfileBody(
                user = profileViewModel.user,
                animeFavorites = profileViewModel.animeFavorites,
                animeReviewed = profileViewModel.animeReviewed,
                onClickAnime = { anime ->
                    animeDetailsViewModel.getAnime(anime.node.id)
                    animeDetailsViewModel.getUserRatings(anime.node.id)
                    navController.navigate("AnimeDetails/${anime.node.id}")
                },
                onClickLogout = {
                    loginViewModel.signOut() // sign out of firebase
                    navController.navigate(SampleScreen.Login.name)
                },
                onClickBack = {
                    navController.navigate(SampleScreen.Overview.name) {
                        popUpTo(SampleScreen.Overview.name) { inclusive = true } // pop off everything up to overview screen
                    }
                },
                onClickReview = { animeReview ->
                    animeDetailsViewModel.getAnime(animeReview.animeData.node.id)
                    animeDetailsViewModel.getUserRatings(animeReview.animeData.node.id)
                    navController.navigate("AnimeDetails/${animeReview.animeData.node.id}")
                }
            )
        }

        composable(SampleScreen.Search.name) {
            SearchScreen(
                animeData =  searchViewModel.animeSearchResults,
                onClickSearch = searchViewModel::getAnimeByName,
                onClickAnime = { anime ->
                    // When user clicks on poster, fetch "One Piece" data and navigate to details
                    animeDetailsViewModel.getAnime(anime.id)
                    animeDetailsViewModel.getUserRatings(anime.id)
                    profileViewModel.getCurrentUser()
                    navController.navigate("AnimeDetails/${anime.id}")
                }
            )
        }
    }
}

