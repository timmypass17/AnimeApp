package com.example.firebasesample.utli


import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.firebasesample.R

object Constants {
    val robotoFamily = FontFamily(
        Font(R.font.roboto_medium, FontWeight.Normal),
        Font(R.font.roboto_thin, FontWeight.Thin)
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

fun getSeasonYear(date: String): String {
    if (date.isEmpty()) {
        return ""
    }
    val date_parts = date.split("-")    // year, month, day
    val year = date_parts[0]
    val month = date_parts[1].toInt()

    val season =
        when(month) {
            in 3..5 -> "Spring"
            in 6..8 -> "Summer"
            in 9..11 -> "Autumn"
            else -> {
                "Winter"
            }
        }

    return "$season $year"
}
fun getMonthDayYear(date: String): String {
    if (date.isEmpty()) {
        return ""
    }
    val date_parts = date.split("-")
    val year = date_parts[0]
    val month_num = date_parts[1]
    val day = date_parts[2]

    val month =
        when(month_num) {
            "01" -> "Jan"
            "02" -> "Feb"
            "03" -> "Mar"
            "04" -> "Apr"
            "05" -> "May"
            "06" -> "June"
            "07" -> "July"
            "08" -> "Aug"
            "09" -> "Sept"
            "10" -> "Oct"
            "11" -> "Nov"
            "12" -> "Dec"
            else -> "month not found"
        }

    return "$month $day, $year"
}