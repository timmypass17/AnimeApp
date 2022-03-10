package com.example.firebasesample.utli

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.firebasesample.R

object Constants {
    val robotoFamily = FontFamily(
        Font(R.font.roboto_medium, FontWeight.Medium)
    )
}

enum class Auth(
    val error: String,
    val message: String
) {
    AccountBad(
        error = "User not found",
        message = "There is no user record corresponding to this identifier. The user may have been deleted."
    ),
    EmailBad(
        error = "Invalid Email",
        message = "The email address is badly formatted."
    ),
    PasswordBad(
        error = "Invalid Password",
        message = "The password is invalid or the user does not have a password."
    );

    companion object {
        fun getError(message: String?): String =
            when (message) {
                AccountBad.message -> AccountBad.error
                EmailBad.message -> EmailBad.error
                PasswordBad.message -> PasswordBad.error
                null -> "how did you get this error"
                else -> message
            }
    }
}