package com.example.algorythm

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.algorythm.API.*
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.example.algorythm.ui.theme.MainTheme
import com.example.algorythm.ui.theme.PurpleGrey40
import com.example.algorythm.ui.theme.PurpleGrey80
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ResetPass1(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundDarkGray
    )

    var email by remember {
        mutableStateOf("")
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Provide email to reset password", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = {
                Text(text = "Email", color = Color.White)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainTheme,
                unfocusedBorderColor = Color.White,
                cursorColor = MainTheme,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                /* TODO */
                if(forgotPassword(email,null))
                {
                    curremail = email
                    navController.navigate(Screens.Reset2.screen)
                }
            },
            colors = ButtonColors(
                containerColor = MainTheme,
                contentColor = Color.White,
                disabledContainerColor = PurpleGrey40,
                disabledContentColor = PurpleGrey80
            ),
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Reset password")
        }
    }
}