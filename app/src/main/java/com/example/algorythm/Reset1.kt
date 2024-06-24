package com.example.algorythm

import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ResetPass1(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = BackgroundDarkGray
    )

    var email by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Provide email to reset password",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
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
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val check = forgotPassword(email)
                            withContext(Dispatchers.Main){
                                Log.e("check", check.toString() )
                                if (check.equals("success")) {
                                    curremail = email
                                    navController.navigate(Screens.Reset2.screen)
                                }
                            }
//                            withContext(Dispatchers.Main) {
//                                navController.navigate(Screens.Reset2.screen)
//                            }
                        } catch (e: Exception) {
                            Log.e("err", e.toString())
                        }
                    }
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