package com.example.algorythm

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.algorythm.ui.theme.AlgorythmTheme
import com.example.algorythm.ui.theme.BackgroundDarkGray

//tymczasowo
var loggedin = false
val streamingConnector = StreamingConnector();
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