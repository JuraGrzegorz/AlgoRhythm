package com.example.algorythm

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.window.Dialog
import author
import bitmap
import com.example.algorythm.API.likeMusic
import com.example.algorythm.API.unlikeMusic
import com.example.algorythm.ui.theme.BackgroundDarkGray
import kotlinx.coroutines.*
import org.json.JSONArray
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.text.input.TextFieldValue
import likes
import musicID
import org.json.JSONObject
import title
import toByteArray
import views

private const val PLAYED_SONGS_PREFS = "played_songs_prefs"
private const val PLAYED_SONGS_KEY = "played_songs_key"
private const val MAX_SONGS = 10

@SuppressLint("DefaultLocale")
@Composable
fun Music() {
    val activity = LocalContext.current as Activity
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var playlists by remember { mutableStateOf(listOf<PlaylistData>()) }
    var showInputDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf(TextFieldValue("")) }
    var isMusicLiked by remember { mutableStateOf("false") }

    var musicID by remember { mutableStateOf( musicID) }

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
            println("EFEKT NULL")
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
            println("EFEKT NO NULL")
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
        println("EFFEKT " + musicID)
        withContext(Dispatchers.IO) {
            try {
                val musicUrl = "https://thewebapiserver20240424215817.azurewebsites.net/Music/GetMusicData?songId=$musicID"
                startPlaying(musicUrl)
            } catch (e: Exception) {
                Log.e("Music", "Error starting music", e)
            }

            withContext(Dispatchers.IO) {
                val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                val jwt = sharedPref.getString("JWT", "") ?: ""
                isMusicLiked = API.isLiked(musicID, jwt)
            }

            savePlayedSong(context, PlayedSong(
                id = musicID,
                title = title,
                author = author,
                thumbnailData = Base64.encodeToString(bitmap?.toByteArray(), Base64.DEFAULT),
                views = views,
                likes = likes
            ))
        }
    }


    suspend fun handleFavoriteButton() {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val jwt = sharedPref.getString("JWT", "") ?: ""
        withContext(Dispatchers.IO) {
            if (likeMusic(musicID, jwt) == null) {
                unlikeMusic(musicID, jwt)
                isMusicLiked = "false"
            } else {
                isMusicLiked = "true"
            }
        }
    }

    fun getPlaylists() {
        coroutineScope.launch {
            try {
                val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                val jwt = sharedPref.getString("JWT", "") ?: ""

                val data: String = withContext(Dispatchers.IO) {
                    API.getUserPlaylists(100, jwt)
                }
                val jsonArray = JSONArray(data)
                val fetchedPlaylists = mutableListOf<PlaylistData>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val playlist = PlaylistData(
                        id = jsonObject.getInt("id"),
                        name = jsonObject.getString("name"),
                        countOfMusic = jsonObject.getInt("countOfMusic")
                    )
                    fetchedPlaylists.add(playlist)
                }
                playlists = fetchedPlaylists
                (playlists as MutableList<PlaylistData>).add(
                    PlaylistData(
                        id = 0, name = "New Playlist", countOfMusic = 1
                    )
                )

            } catch (e: Exception) {
                Log.e("Music", "Error fetching playlists", e)
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
                    bitmap = bitmap!!.asImageBitmap(),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = views + " views",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = likes + " likes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Box(modifier = Modifier.align(Alignment.Start)) {
            Text(
                text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(modifier = Modifier.align(Alignment.Start)) {
            Text(
                text = author, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val mostRecentSong = loadMostRecentPlayedSong(context)

                    if (mostRecentSong != null) {
                        stopPlaying()
                        musicID = mostRecentSong.id
                        title = mostRecentSong.title
                        author = mostRecentSong.author
                        views = mostRecentSong.views
                        likes = mostRecentSong.likes

                        val imageBytes = Base64.decode(mostRecentSong.thumbnailData, Base64.DEFAULT)
                        bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)


                    }
                }
            })
            {
                Image(
                    painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                    contentDescription = "Previous",
                    modifier = Modifier.size(120.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(onClick = {
                if (isPlaying) {
                    mediaPlayer?.pause()
                } else {
                    mediaPlayer?.start()
                }
                isPlaying = !isPlaying
            }) {
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
            IconButton(onClick = {
                /* Next track logic */

                stopPlaying()
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                        val jwt = sharedPref.getString("JWT", "") ?: ""
                        val data: String = API.getProposedMusic(1, jwt)
                        val arr = JSONArray(data)
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            val nextid = obj.getString("id")
                            val nextTitle = obj.getString("title")
                            val nextAuthor = obj.getString("artistName")
                            val thumbnailData = obj.getString("thumbnailData")
                            val nextViews = obj.getString("views")
                            val nextLikes = obj.getString("likes")
                            val imageBytes = Base64.decode(thumbnailData, Base64.DEFAULT)
                            val nextBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            musicID = nextid
                            title = nextTitle
                            author = nextAuthor
                            views = nextViews
                            likes = nextLikes
                            bitmap = nextBitmap
                        }




                    }catch (_: Exception) {

                }
                    }






            }) {
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
            IconButton(onClick = {
                getPlaylists()
                showDialog = true
            }) {
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
                if (isMusicLiked == "true") {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_fav_star_24),
                        contentDescription = "Star",
                        modifier = Modifier.size(50.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_unfav_star_24),
                        contentDescription = "Star",
                        modifier = Modifier.size(50.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
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
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.align(Alignment.Start)) {
            val currentMinutes = (currentPosition / 1000) / 60
            val currentSeconds = (currentPosition / 1000) % 60
            val durationMinutes = (duration / 1000) / 60
            val durationSeconds = (duration / 1000) % 60
            Text(
                text = String.format(
                    "%02d:%02d / %02d:%02d",
                    currentMinutes,
                    currentSeconds,
                    durationMinutes,
                    durationSeconds
                ), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White
            )
        }
    }

    if (showDialog) {
        PlaylistDialog(playlists = playlists,
            onDismissRequest = { showDialog = false },
            onPlaylistClick = { playlistId, playlistName ->
                if (playlistId != 0) {
                    coroutineScope.launch {
                        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                        val jwt = sharedPref.getString("JWT", "") ?: ""
                        withContext(Dispatchers.IO) {
                            API.addToPlaylist(playlistId, musicID, jwt)
                        }

                        showDialog = false
                        Toast.makeText(
                            context, "Song added to playlist " + playlistName, Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    showInputDialog = true
                    showDialog = false
                }
            })
    }

    if (showInputDialog) {
        NewPlaylistDialog(newPlaylistName = newPlaylistName,
            onNameChange = { newPlaylistName = it },
            onDismissRequest = { showInputDialog = false },
            onCreatePlaylist = {
                showInputDialog = false
                coroutineScope.launch {
                    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                    val jwt = sharedPref.getString("JWT", "") ?: ""

                    withContext(Dispatchers.IO) {
                        API.createPlaylist(newPlaylistName.text, musicID, jwt)
                    }


                }
            })
    }

    DisposableEffect(Unit) {
        onDispose {
            stopPlaying()
        }
    }
}

@Composable
fun PlaylistDialog(
    playlists: List<PlaylistData>,
    onDismissRequest: () -> Unit,
    onPlaylistClick: (Int, String) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Select Playlist",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(playlists) { playlist ->
                        Text(
                            text = playlist.name,
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onPlaylistClick(playlist.id, playlist.name)
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

data class PlayedSong(
    val id: String,
    val title: String,
    val author: String,
    val thumbnailData: String,
    val views: String,
    val likes: String
)

fun loadMostRecentPlayedSong(context: Context): PlayedSong? {
    val prefs = context.getSharedPreferences(PLAYED_SONGS_PREFS, Context.MODE_PRIVATE)
    val songsJson = prefs.getString(PLAYED_SONGS_KEY, null) ?: return null
    val songsArray = JSONArray(songsJson)

    if (songsArray.length() == 0) return null

    val mostRecentSong = songsArray.getJSONObject(songsArray.length() - 2)
    return PlayedSong(
        id = mostRecentSong.getString("id"),
        title = mostRecentSong.getString("title"),
        author = mostRecentSong.getString("author"),
        thumbnailData = mostRecentSong.getString("thumbnailData"),
        views = mostRecentSong.getString("views"),
        likes = mostRecentSong.getString("likes")
    )
}

fun savePlayedSong(context: Context, song: PlayedSong) {
    val prefs = context.getSharedPreferences(PLAYED_SONGS_PREFS, Context.MODE_PRIVATE)
    val editor = prefs.edit()

    val songsJson = prefs.getString(PLAYED_SONGS_KEY, "[]")
    val songsArray = JSONArray(songsJson)

    for (i in 0 until songsArray.length()) {
        val existingSong = songsArray.getJSONObject(i)
        if (existingSong.getString("id") == song.id) {
            songsArray.remove(i)
            break
        }
    }

    if (songsArray.length() >= MAX_SONGS) {
        songsArray.remove(0)
    }

    val songJson = JSONObject().apply {
        put("id", song.id)
        put("title", song.title)
        put("author", song.author)
        put("thumbnailData", song.thumbnailData)
        put("views", song.views)
        put("likes", song.likes)
    }
    songsArray.put(songJson)

    editor.putString(PLAYED_SONGS_KEY, songsArray.toString())
    editor.apply()
}

@Composable
fun NewPlaylistDialog(
    newPlaylistName: TextFieldValue,
    onNameChange: (TextFieldValue) -> Unit,
    onDismissRequest: () -> Unit,
    onCreatePlaylist: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "New Playlist",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                BasicTextField(
                    value = newPlaylistName,
                    onValueChange = onNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White,
                        modifier = Modifier
                            .clickable { onDismissRequest() }
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create",
                        color = Color.White,
                        modifier = Modifier
                            .clickable { onCreatePlaylist() }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
