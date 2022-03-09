package com.example.firebasesample.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel() : ViewModel() {

//    private val _email = mutableStateOf("")
//    val email = _email.value
//
//    private val _password = mutableStateOf("")
//    val password = _password.value


    fun displayEmailPass(email: String, password: String) {
        Log.i("LoginViewModel", "$email, $password")
    }

}