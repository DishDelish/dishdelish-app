package com.example.kotlinandjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinandjetpackcompose.ui.AppViewModel
import com.example.kotlinandjetpackcompose.ui.greetingScreen
import com.example.kotlinandjetpackcompose.ui.mainScreen
import com.example.kotlinandjetpackcompose.ui.theme.KotlinAndJetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinAndJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    App()

                }
            }
        }
    }
}

/**
 * enum values that represent the screens in the app
 */
enum class AppScreen() {
    MainScreen,
    GreetingScreen
}

@Composable
fun App(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppScreen.MainScreen.name
    ) {
        composable(route = AppScreen.MainScreen.name) {
            mainScreen(
                onClick = {
                    viewModel.setUserName(it)
                    navController.navigate(AppScreen.GreetingScreen.name)
                }
            )
        }
        composable(route = AppScreen.GreetingScreen.name) {
            greetingScreen(
                userName = uiState
            )
        }
    }
}