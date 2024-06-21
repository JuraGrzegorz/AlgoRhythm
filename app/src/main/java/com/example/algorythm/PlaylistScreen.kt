import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorythm.API.getLikedMusic
import com.example.algorythm.API.getPlaylistMusic
import com.example.algorythm.Music
import com.example.algorythm.R
import com.example.algorythm.SongItem
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import com.example.algorythm.API.getPlaylistThumbnail
import com.example.algorythm.Screens
import kotlinx.coroutines.withContext
private const val songAmount = 100


@Composable
fun PlaylistScreen(navController: NavController, playlistName: String, id: Int) {
    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    systemUiController.setSystemBarsColor(
        color = Color.Black
    )
    val context = LocalContext.current
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    var songs by remember { mutableStateOf(listOf<Song>()) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var playlistThumbnail by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                var jwt = ""
                jwt = sharedPref.getString("JWT", "") ?: ""
                Log.e("jwt", jwt)
                var data: String = "null"

                if (id == 0) {
                    data = getLikedMusic(songAmount, jwt)
                    println("ID STJEST 0")
                } else {
                    data = getPlaylistMusic(id, 0, songAmount, jwt)
                    println("ID NIE AJEST 0" + id)
                }
                println(data)
                val arr = JSONArray(data)
                val songList = mutableListOf<Song>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val id = obj.getString("id")
                    val title = obj.getString("title")
                    val author = obj.getString("artistName")
                    val thumbnailData = obj.getString("thumbnailData")
                    val imageBytes = Base64.decode(thumbnailData, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    songList.add(Song(id, title, author, bitmap))
                }
                songs = songList

                // Fetch playlist thumbnail
                val thumbnailData = getPlaylistThumbnail(id, jwt)
                val imageBytes = Base64.decode(thumbnailData, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                withContext(Dispatchers.Main) {
                    playlistThumbnail = bitmap
                }
            } catch (_: Exception) {
                // Handle error
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
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    playlistThumbnail?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                        )
                    } ?: Image(
                        painter = painterResource(id = R.drawable.fav_playlist_thumnail),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                    )
                    Text(
                        text = playlistName,
                        fontSize = 30.sp,
                        color = Color.White,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black, offset = Offset(2f, 2f), blurRadius = 4f
                            )
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = { /* Share functionality */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_share_24),
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { /* Play functionality */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_play_circle_24),
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
                thickness = 1.dp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(songs) { song ->
                    SongItem(bitmap = song.thumbnail,
                        title = song.title,
                        author = song.author,
                        onClick = {
                            selectedSong = song
                        })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    selectedSong?.let {
        title = it.title
        author = it.author
        musicID = it.id
        bitmap = it.thumbnail
//        Music(title = it.title, author = it.author, musicID = it.id, bitmap = it.thumbnail)
        navController.navigate(Screens.Music.screen)
        selectedSong = null
    }
}
