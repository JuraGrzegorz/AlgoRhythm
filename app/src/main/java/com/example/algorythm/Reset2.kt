package com.example.algorythm

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun ResetPass2(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    systemUiController.setSystemBarsColor(
        color = BackgroundDarkGray
    )


    var code by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var passwordConfirmation by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Reset Password", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = code, onValueChange = { code = it },
            label = {
                Text(text = "Enter code from email", color = Color.White)
            },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainTheme,
                unfocusedBorderColor = Color.White,
                cursorColor = MainTheme,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = {
                Text(text = "New password", color = Color.White)
            },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MainTheme,
                unfocusedBorderColor = Color.White,
                cursorColor = MainTheme,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = passwordConfirmation, onValueChange = { passwordConfirmation = it },
            label = {
                Text(text = "Confirm new password", color = Color.White)
            },
            visualTransformation = PasswordVisualTransformation(),
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
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val check =  verifyForgotPasswordCode(curremail,code,password)
                            withContext(Dispatchers.Main){
                                Log.e("check", check.toString() )
//                                if (!check.isNullOrEmpty()) {
                                    navController.navigate(Screens.SignInScreen.screen)
//                                }
//                                else
//                                {
//                                    Toast.makeText(
//                                        context,
//                                        "Please check your credentials.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
                            }
                        } catch (e: Exception) {
                            Log.e("err", e.toString())
                        }
                    }
                }

//                if(password == passwordConfirmation)
//                {
//                    verifyForgotPasswordCode(curremail,code,password)
//                    navController.navigate(Screens.SignInScreen.screen)
//                }
//                else
//                {
//                    Toast.makeText(
//                        context,
//                        "Please check your credentials.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }

            },
            colors = ButtonColors(
                containerColor = MainTheme,
                contentColor = Color.White,
                disabledContainerColor = PurpleGrey40,
                disabledContentColor = PurpleGrey80
            ),
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Change to new password",textAlign = TextAlign.Center,fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            val check = forgotPassword(curremail)
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
            )
        ) {
            Text(text = "Get email with code",textAlign = TextAlign.Center,fontSize = 15.sp)
        }
    }
}