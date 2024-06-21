package com.example.algorythm

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SignInNavigation(){
    val navigationController = rememberNavController()
    var startDest  = Screens.Home.screen
    val activity = LocalContext.current as Activity
    var jwt = ""
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    jwt = sharedPref.getString("JWT","") ?:""
    if (jwt.isEmpty()) startDest = Screens.SignInScreen.screen
    NavHost(
        navController = navigationController,
        startDestination = startDest,
    ) {
        composable(Screens.SignInScreen.screen) { SignInScreen(navigationController) }
        composable(Screens.SignUpScreen.screen) { SignUpScreen(navigationController) }
        composable(Screens.Home.screen) { ScaffoldExample()}
        composable(Screens.Reset1.screen) { ResetPass1(navigationController) }
        composable(Screens.Reset2.screen) { ResetPass2(navigationController) }
    }
}

