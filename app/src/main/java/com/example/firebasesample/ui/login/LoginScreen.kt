package com.example.firebasesample.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasesample.SampleScreen

@Composable
fun LoginBody(
    onClickLogin: () -> Unit = {},
    onClickSignUp: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", fontSize = 30.sp)
        AccountCard()
        Spacer(modifier = Modifier.padding(8.dp))
        LoginButton(onClickLogin = onClickLogin)
        Spacer(modifier = Modifier.padding(8.dp))
        SignUpButton(onClickSignUp = onClickSignUp)
    }
}

@Composable
fun AccountCard() {
    Column() {
        EmailTextField()
        PasswordTextField()
    }
}

@Composable
fun EmailTextField() {
    var email by rememberSaveable { mutableStateOf("") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Enter email") },
        singleLine = true
    )
}

@Composable
fun PasswordTextField() {
    var password by rememberSaveable { mutableStateOf("") }

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Enter password") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Composable
fun LoginButton(onClickLogin: () -> Unit) {
    Button(
        onClick = { onClickLogin() }
    ) {
        Icon(
            Icons.Filled.AccountBox,
            contentDescription = "Favorite",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text("Login")
    }
}

@Composable
fun SignUpButton(onClickSignUp: () -> Unit) {
    TextButton(
        onClick = { onClickSignUp() }
    ) {
        Text("Sign Up")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginBody()
}