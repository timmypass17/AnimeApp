package com.example.firebasesample.data.impl

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.firebasesample.data.SignUpRepository
import com.example.firebasesample.ui.signup.SignUpBody
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val TAG = "SignUpRepositoryImpl"
class SignUpRepositoryImpl(): SignUpRepository {
    override fun createAccount(email: String, password: String) {

    }


}