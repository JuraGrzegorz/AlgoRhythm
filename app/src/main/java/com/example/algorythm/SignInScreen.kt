package com.example.algorythm

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavController
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.example.algorythm.ui.theme.MainTheme
import com.example.algorythm.ui.theme.PurpleGrey40
import com.example.algorythm.ui.theme.PurpleGrey80
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

@Composable
fun SignInScreen(navController: NavController) {
    val activity = LocalContext.current as Activity
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    systemUiController.setSystemBarsColor(
        color = BackgroundDarkGray
    )
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()

    fun performLogin() {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {

                    val map = API.loginUser(email, password)
                    loggedin = !map.isNullOrEmpty()
                    withContext(Dispatchers.Main) {
                        if (loggedin) {
                            val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                            with (sharedPref.edit()) {
                                putString("JWT", map["token"])
                                putString("username", map["username"])
                                apply()
                            }
                            navController.navigate(Screens.Home.screen)
                        } else {
                            Toast.makeText(
                                context,
                                "Login failed. Please check your credentials.",
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
        Text(
            text = "Sign In",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
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
            value = password,
            onValueChange = { password = it.trim() },
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
        Button(
            onClick = {
                performLogin()
//                navController.navigate(Screens.Home.screen)
            },
            colors = ButtonColors(
                containerColor = MainTheme,
                contentColor = Color.White,
                disabledContainerColor = PurpleGrey40,
                disabledContentColor = PurpleGrey80
            ),
            modifier = Modifier.width(200.dp)
        ) {
            Text(text = "Login")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Reset password", Modifier.clickable {
            navController.navigate(Screens.Reset1.screen)
        }, color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "First time here?", fontSize = 20.sp, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                navController.navigate(Screens.SignUpScreen.screen)
            },
            colors = ButtonColors(
                containerColor = MainTheme,
                contentColor = Color.White,
                disabledContainerColor = PurpleGrey40,
                disabledContentColor = PurpleGrey80
            )
        ) {
            Text(text = "Sign Up!")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "or sign in with", color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            val credentialManager = CredentialManager.create(context)
            val rawNounce = UUID.randomUUID().toString()
            val bytes = rawNounce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNounce = digest.fold("") {str, it -> str + "%02x".format(it)}

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("864690848376-prjgp8h699v4np8cpne2t89imqfhqv2h.apps.googleusercontent.com")
                .setNonce(hashedNounce)
                .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            coroutineScope.launch {

                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context
                    )
                    val credential = result.credential

                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    val  googleIdToken = googleIdTokenCredential.idToken

                    withContext(Dispatchers.IO) {
                        try {

                            val map = API.googleTokenVerification(googleIdToken)
                            loggedin = !map.isNullOrEmpty()
                            withContext(Dispatchers.Main) {
                                if (loggedin) {
                                    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                                    with (sharedPref.edit()) {
                                        putString("JWT", map["token"])
                                        putString("username", map["username"])
                                        apply()
                                    }
                                    navController.navigate(Screens.Home.screen)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Login failed. Please check your credentials.",
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
                catch (e : GetCredentialException)
                {
                    Log.e("credenitalEx",e.toString())
                }
                catch (e : GoogleIdTokenParsingException)
                {
                    Log.e("googleparsEx",e.toString())
                }
            }
        }) {
            Text(text = "Sign in with google")
        }
//        Image(
//            painter = painterResource(id = R.drawable.google_icon),
//            contentDescription = "Google",
//            modifier = Modifier
//                .size(60.dp)
//                .clickable {
//                }
//        )
    }
}

