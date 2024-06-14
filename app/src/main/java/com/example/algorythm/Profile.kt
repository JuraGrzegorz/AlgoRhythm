package com.example.algorythm

import Song
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.example.algorythm.ui.theme.MainTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

@Composable
fun Profile(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Black
    )
    val activity = LocalContext.current as Activity
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    val username = sharedPref.getString("username", "") ?: ""
    val coroutineScope = rememberCoroutineScope()

    var playlists by remember { mutableStateOf(listOf<PlaylistData>()) }
    var playlistThumbnails by remember { mutableStateOf(mapOf<Int, Bitmap?>()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val jwt = sharedPref.getString("JWT", "") ?: ""

                val data: String = API.getUserPlaylists(100, jwt)
                val jsonArray = JSONArray(data)
                val fetchedPlaylists = mutableListOf<PlaylistData>()
                val thumbnails = mutableMapOf<Int, Bitmap?>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val playlist = PlaylistData(
                        id = jsonObject.getInt("id"),
                        name = jsonObject.getString("name"),
                        countOfMusic = jsonObject.getInt("countOfMusic")
                    )
                    fetchedPlaylists.add(playlist)
                    try {
                        val thumbnailData = API.getPlaylistThumbnail(playlist.id, jwt)
                        val imageBytes = Base64.decode(thumbnailData, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        thumbnails[playlist.id] = bitmap
                    } catch (e: Exception) {
                        thumbnails[playlist.id] = null
                    }
                }
                playlists = fetchedPlaylists // Update the state here
                playlistThumbnails = thumbnails
            } catch (e: Exception) {
                Log.e("Profile", "Error fetching playlists", e)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDarkGray)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MainTheme, BackgroundDarkGray,
                            )
                        )
                    )
            ) {
                Text(
                    text = username.split("@")[0],
                    fontSize = 27.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                )

                IconButton(
                    onClick = { /* Handle button click */ },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ui_settings),
                        contentDescription = "More options",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 50.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterHorizontally)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                PlaylistItem(
                    bitmap = null,
                    placeholderResId = R.drawable.fav_playlist_thumnail,
                    title = "Favourite tracks",
                    onClick = {
                        navController.navigate("playlist/Favourite tracks/${0}")
                    },
                    onButtonClick = { /* Handle button click */ }
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
                thickness = 1.dp,
                color = Color.White
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                println("playlisty" + playlists.size)
                items(playlists) { playlist ->
                    val thumbnail = playlistThumbnails[playlist.id]
                    PlaylistItem(
                        bitmap = thumbnail,
                        placeholderResId = R.drawable.logo_placeholder,
                        title = playlist.name,
                        onClick = {
                            navController.navigate("playlist/${playlist.name}/${playlist.id}")
                        },
                        onButtonClick = { /* Handle button click */ }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

data class PlaylistData(val id: Int, val name: String, val countOfMusic: Int)