package com.example.algorythm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.algorythm.ui.theme.AlgorythmTheme
import com.example.algorythm.ui.theme.BackgroundDarkGray


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlgorythmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDarkGray
                ) {
//                    ScaffoldExample()
                    SignInNavigation()
                }
            }
        }
    }
}