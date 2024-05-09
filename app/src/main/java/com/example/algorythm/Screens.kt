package com.example.algorythm

sealed class Screens (val screen: String){
    data object Home: Screens("home")
    data object Search: Screens("search")
    data object Profile: Screens("profile")
    data object SignInScreen: Screens("SignIn")
    data object SignUpScreen: Screens("SignUp")
    data object Music: Screens("Music")
}