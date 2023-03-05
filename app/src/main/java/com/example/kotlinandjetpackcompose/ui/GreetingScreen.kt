package com.example.kotlinandjetpackcompose.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun greetingScreen(
    userName: String
) {
    Text("Bonjour $userName!")
}