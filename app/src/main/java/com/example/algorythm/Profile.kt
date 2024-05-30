package com.example.algorythm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.example.algorythm.ui.theme.MainTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

@Composable
fun Profile() {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Black
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDarkGray)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MainTheme, BackgroundDarkGray,
                            )
                        )
                    )
            ) {
                Text(
                    text = "username",
                    fontSize = 27.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                )

                IconButton(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ui_settings),
                        contentDescription = "More options",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 50.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterHorizontally)
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                PlaylistItem(
                    imageResId = R.drawable.logo_placeholder,
                    title = "Favourite tracks",
                    onClick = {/* Handle on playlist click */},
                    onButtonClick = { /* Handle button click */ }
                )
            }


            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
                thickness = 1.dp,
                color = Color.White
            )


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(10) { index ->
                    PlaylistItem(
                        imageResId = R.drawable.logo_placeholder,
                        title = "Playlist $index",
                        onClick = {/* Handle on playlist click */},
                        onButtonClick = { /* Handle button click */ }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
