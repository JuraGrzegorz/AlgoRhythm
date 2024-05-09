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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.example.algorythm.ui.theme.MainTheme
import com.example.algorythm.ui.theme.PurpleGrey40
import com.example.algorythm.ui.theme.PurpleGrey80
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignUpScreen(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    systemUiController.setSystemBarsColor(
        color = BackgroundDarkGray
    )

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }
    var passwordConfirmation by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    fun performRegistration() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if(password.equals(passwordConfirmation)) loggedin = LoginEndpoints.registerUser(email,password)
                    else loggedin = false;
                    withContext(Dispatchers.Main) {
                        if (loggedin) {
                            navController.navigate(Screens.Home.screen)
                        } else {
                            Toast.makeText(
                                context,
                                "Register failed. Please check your credentials.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "An error occurred: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Sign Up", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = {
                Text(text = "Password", color = Color.White)
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
                Text(text = "Confirm password", color = Color.White)
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
                performRegistration()
            },
            colors = ButtonColors(
                containerColor = MainTheme,
                contentColor = Color.White,
                disabledContainerColor = PurpleGrey40,
                disabledContentColor = PurpleGrey80
            ),
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Sign up")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Already have a account?", fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                navController.navigate(Screens.SignInScreen.screen)
            },
            colors = ButtonColors(
                containerColor = MainTheme,
                contentColor = Color.White,
                disabledContainerColor = PurpleGrey40,
                disabledContentColor = PurpleGrey80
            )
        ) {
            Text(text = "Sign In!")
        }
    }
}