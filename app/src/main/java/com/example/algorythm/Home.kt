package com.example.algorythm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun Home(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Black
    )
    Box(modifier = Modifier
        .fillMaxSize()
        .background(BackgroundDarkGray)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(text = "Home", fontSize = 30.sp, color = Color.White)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp)
                .background(Color.Black, RoundedCornerShape(16.dp))
                .align(Alignment.BottomCenter)
        ) {
            MusicPlayer(navController)
        }

    }
}