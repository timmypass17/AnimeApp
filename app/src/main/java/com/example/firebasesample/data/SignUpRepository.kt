package com.example.firebasesample.data

import com.example.firebasesample.Response
import kotlinx.coroutines.flow.Flow

interface SignUpRepository {

    fun createAccount(email: String, password: String)
}