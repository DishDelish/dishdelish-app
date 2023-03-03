package com.github.siela1915.bootcamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.siela1915.bootcamp.ui.theme.SDPBootcampTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SDPBootcampTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainActivityNavHost()
                }
            }
        }
    }
}

@Composable
fun MainActivityNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("main") {
            MainScreen(onNavigateToGreeting = { name -> {
                navController.navigate("greeting?name=$name")
            }})
        }
        composable(
            route = "greeting?name={name}"
        ) { backStackEntry ->
            GreetingScreen(name = backStackEntry.arguments?.getString("name"))
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigateToGreeting: (String) -> () -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = text,
            onValueChange = { newText ->
                text = newText
            },
            placeholder = { Text(text = "Your name") },
            modifier = Modifier.semantics { testTag = "nameInputField" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToGreeting(text.text),
            modifier = Modifier.semantics { testTag = "goGreetingButton" }
        ) {
            Text(text = "Go")
        }
    }
}

@Composable
fun GreetingScreen(
    modifier: Modifier = Modifier,
    name: String?
) {
    Column(
        modifier = modifier.
        padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Hello $name!", modifier = Modifier.semantics { testTag = "greetingText" })
    }
}