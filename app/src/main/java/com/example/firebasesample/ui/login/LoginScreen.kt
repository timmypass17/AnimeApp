package com.example.firebasesample.ui.login

import android.graphics.fonts.FontStyle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasesample.R
import com.example.firebasesample.utli.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginBody(
    onClickLogin: (email: String, password: String) -> Unit,
    onClickGoogleSignIn: (String) -> Unit,
    onClickSignUp: () -> Unit,
    validEmail: Boolean,
    validPassword: Boolean,
    errorMessage: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(modifier = Modifier.padding(8.dp), text = "Sign In", fontSize = 30.sp)

        /** Login user input **/
        SignInEmailPasswordCard(
            onClickLogin = onClickLogin,
            validEmail = validEmail,
            validPassword = validPassword,
            errorMessage = errorMessage)

        Text(modifier = Modifier.padding(8.dp), text = "or", fontStyle = Italic, color = Color.Gray)

        /** Google Sign in **/
        GoogleSignInButton(onClickSignIn = onClickGoogleSignIn)

        /** SignUp Button **/
        SignUpButton(onClickSignUp = onClickSignUp)
    }
}

@Composable
fun SignInEmailPasswordCard(
    onClickLogin: (email: String, password: String) -> Unit,
    validEmail: Boolean,
    validPassword: Boolean,
    errorMessage: String
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    /** Account text fields **/
    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Enter email") },
        singleLine = true,
        isError = !validEmail
    )
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Enter password") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = !validPassword
    )

    /** Error Message **/
    if (!validEmail or !validPassword) {
        Text(modifier = Modifier.padding(8.dp), text = errorMessage, color = MaterialTheme.colors.error)
    } else {
        Spacer(modifier = Modifier.padding(8.dp))
    }

    /** Login Button**/
    Button(
        onClick = { onClickLogin(email.trim(), password.trim()) },
        enabled = email.isNotBlank() and password.isNotBlank()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_login),
            contentDescription = "Favorite",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text("Sign in")
    }
}

@Composable
fun SignUpButton(onClickSignUp: () -> Unit) {
    TextButton(modifier = Modifier.padding(8.dp),
        onClick = { onClickSignUp() }
    ) {
        Text("Don't have an account? Sign up")
    }
}

@Composable
fun GoogleSignInButton(onClickSignIn: (String) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                onClickSignIn(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("TAG", "Google sign in failed", e)
            }
        })

    val context = LocalContext.current
    val token = stringResource(R.string.oauth_id)
    OutlinedButton(onClick = {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        launcher.launch(googleSignInClient.signInIntent)
    }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google sign in",
            tint = Color.Unspecified)
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = "Sign in with Google",
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = Constants.robotoFamily,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginBody(
        onClickLogin = ::fakeLogin,
        onClickSignUp = {},
        validEmail = true,
        validPassword = true,
        errorMessage = "Error Message",
        onClickGoogleSignIn = ::fakeGoogleLogin
    )
}

fun fakeLogin(fake_email: String, fake_password: String) {}
fun fakeGoogleLogin(fake_email: String) {}