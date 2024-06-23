package com.example.algorythm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.algorythm.ui.theme.AlgorythmTheme
import com.example.algorythm.ui.theme.BackgroundDarkGray

//tymczasowo
var loggedin = false
var curremail = ""



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlgorythmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDarkGray
                ) {
                    SignInNavigation()
                }
            }
        }
    }
}

fun restartApp(context: Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    context.startActivity(intent)
    (context as? Activity)?.finish()
}