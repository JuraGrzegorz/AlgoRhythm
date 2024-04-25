package com.example.algorythm

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SignInNavigation(){
    val navigationController = rememberNavController()

    NavHost(
        navController = navigationController,
        startDestination = Screens.SignInScreen.screen,
    ) {
        composable(Screens.SignInScreen.screen) { SignInScreen(navigationController) }
        composable(Screens.SignUpScreen.screen) { SignUpScreen(navigationController) }

    }
}

