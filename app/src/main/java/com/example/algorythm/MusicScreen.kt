package com.example.algorythm

import android.graphics.Bitmap
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorythm.API.likeMusic
import com.example.algorythm.API.unlikeMusic
import com.example.algorythm.ui.theme.BackgroundDarkGray
import kotlinx.coroutines.*

@Composable
fun Music(
    title: String,
    author: String,
    musicID: String,
    bitmap: Bitmap?,
) {
    val activity = LocalContext.current as Activity
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    fun startSeekBarUpdate() {
        coroutineScope.launch {
            while (mediaPlayer?.isPlaying == true) {
                currentPosition = mediaPlayer?.currentPosition ?: 0
                delay(1000)
            }
        }
    }

    fun startPlaying(url: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener {
                    start()
                    duration = mediaPlayer?.duration ?: 0
                    isPlaying = true
                    startSeekBarUpdate()
                }
                setOnCompletionListener {
                    isPlaying = false
                }
                prepareAsync()
            }
        } else {
            mediaPlayer?.start()
            isPlaying = true
            startSeekBarUpdate()
        }
    }

    fun stopPlaying() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    LaunchedEffect(musicID) {
        withContext(Dispatchers.IO) {
            try {
                val musicUrl =
                    "https://thewebapiserver20240424215817.azurewebsites.net/test/GetMusicData?songId=$musicID"
                startPlaying(musicUrl)
            } catch (e: Exception) {
                Log.e("Music", "Error starting music", e)
            }
        }
    }

    suspend fun handleFavoriteButton() {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val jwt = sharedPref.getString("JWT", "") ?: ""
        withContext(Dispatchers.IO) {
            if (likeMusic(musicID, jwt) == null){
                unlikeMusic(musicID, jwt)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDarkGray)
            .padding(horizontal = 20.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .size(300.dp)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "MusicImg",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.align(Alignment.Start)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(modifier = Modifier.align(Alignment.Start)) {
            Text(
                text = author,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Previous track logic */ }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                    contentDescription = "Previous",
                    modifier = Modifier.size(120.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                    } else {
                        mediaPlayer?.start()
                    }
                    isPlaying = !isPlaying
                }
            ) {
                Image(
                    painter = if (isPlaying) painterResource(id = R.drawable.baseline_pause_circle_24) else painterResource(
                        id = R.drawable.baseline_play_circle_24
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(180.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                onClick = { /* Next track logic */ }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_skip_next_24),
                    contentDescription = "Next",
                    modifier = Modifier.size(120.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Plus logic */ }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Plus",
                    modifier = Modifier.size(50.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        handleFavoriteButton()
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_fav_star_24),
                    contentDescription = "Star",
                    modifier = Modifier.size(50.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Slider(
            value = if (duration > 0) currentPosition / duration.toFloat() else 0f,
            onValueChange = { newValue ->
                val newPosition = (newValue * duration).toInt()
                mediaPlayer?.seekTo(newPosition)
                currentPosition = newPosition
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.align(Alignment.Start)) {
            Text(
                text = (currentPosition / 1000).toString() + "/" + (duration / 1000).toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            stopPlaying()
        }
    }
}
