package com.example.firebasesample

import androidx.compose.ui.graphics.vector.ImageVector

enum class SampleScreen(
) {
    Login(),
    SignUp(),
    Overview();

    companion object {
        fun fromRoute(route: String?): SampleScreen =
            when (route?.substringBefore("/")) {
                Login.name -> Login
                SignUp.name -> SignUp
                Overview.name -> Overview
                null -> Login
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}