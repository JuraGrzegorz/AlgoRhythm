package com.example.algorythm

import android.graphics.Bitmap
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
        composable(Screens.Home.screen) { ScaffoldExample(navigationController)}
        composable(Screens.Music.screen) {  Music(
            title = "title",
            author = "author",
            musicID = "15",
            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888),
        ) }
        composable(Screens.Reset1.screen) { ResetPass1(navigationController) }
        composable(Screens.Reset2.screen) { ResetPass2(navigationController) }
    }
}

