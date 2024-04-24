package com.example.algorythm

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MusicPlayer() {
    var isPlaying by remember { mutableStateOf(false) }
    var songName by remember { mutableStateOf("Song Name") }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = songName, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = {
                isPlaying = !isPlaying
            }
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Clear else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }
    }
}
