package com.example.algorythm

import android.graphics.Bitmap
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.algorythm.ui.theme.BackgroundDarkGray
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun Music(
    title: String,
    author: String,
    musicID: String,
    bitmap: Bitmap?,
)
{
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = MediaPlayer()
    val cache = LocalContext.current.applicationContext.cacheDir
    val coroutineScope = rememberCoroutineScope()

    fun initMusic() {
        coroutineScope.launch {
            try {
                streamingConnector.start(musicID)
                val tempMp3: File = File.createTempFile("tempfile", "mp3", cache)
                tempMp3.deleteOnExit()
                val fos: FileOutputStream = FileOutputStream(tempMp3)
                fos.write(streamingConnector.musicData)
                fos.close()

                mediaPlayer.reset()

                val fis: FileInputStream = FileInputStream(tempMp3)
                mediaPlayer.setDataSource(fis.fd)

                mediaPlayer.prepareAsync()
                mediaPlayer.start()
            } catch (ex: IOException) {
                val s: String = ex.toString()
                ex.printStackTrace()
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
                onClick = {
                    /* TODO: */
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                    contentDescription = "LeftArrow",
                    modifier = Modifier.size(120.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                onClick = {
                    if (isPlaying) {
                        mediaPlayer.stop()
                    } else {
                        mediaPlayer.start()
                    }
                    isPlaying = !isPlaying
                }
            ) {
                Image(
                    painter = if (isPlaying) painterResource(id = R.drawable.baseline_play_circle_24) else painterResource(id = R.drawable.baseline_pause_circle_24),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(180.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                onClick = {
                    /* TODO: */
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_skip_next_24),
                    contentDescription = "RightArrow",
                    modifier = Modifier.size(120.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }
    }

    LaunchedEffect(true) {
        initMusic()
    }
}