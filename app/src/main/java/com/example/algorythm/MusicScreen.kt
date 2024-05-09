package com.example.algorythm

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Music() {
    var isPlaying by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .size(300.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.tempmusic),
                contentDescription = "MusicImg",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.align(Alignment.Start)) {
            Text(
                text = "Music name",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(modifier = Modifier.align(Alignment.Start)) {
            Text(
                text = "Album name",
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
}
