import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.algorythm.API.getProposedMusic
import com.example.algorythm.Music
import com.example.algorythm.PlaylistData
import com.example.algorythm.Screens
import com.example.algorythm.SongItem
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.example.algorythm.ui.theme.MainTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

private const val songAmount = 10
var currentPlaylistId = -1;
var title = ""
var author = ""
var musicID = ""
var bitmap: Bitmap? = null
var views = ""
var likes = ""
var duration = ""
var currentPosition = ""

@Composable
fun Home(navController: NavHostController) {
    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)

    val username = sharedPref.getString("username", "") ?: ""
    systemUiController.setSystemBarsColor(
        color = Color.Black
    )
    val context = LocalContext.current
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    var songs by remember { mutableStateOf(listOf<Song>()) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                var jwt = ""
                jwt = sharedPref.getString("JWT", "") ?: ""
                Log.e("jwt", jwt)
                val data: String = getProposedMusic(songAmount, jwt)
                println(data)
                val arr = JSONArray(data)
                val songList = mutableListOf<Song>()
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val id = obj.getString("id")
                    val title = obj.getString("title")
                    val author = obj.getString("artistName")
                    val thumbnailData = obj.getString("thumbnailData")
                    val views = obj.getString("views")
                    val likes = obj.getString("likes")
                    val imageBytes = Base64.decode(thumbnailData, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    songList.add(Song(id, title, author, bitmap, views, likes, "null"))
                }
                songs = songList
            } catch (_: Exception) {

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
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Welcome " + username.split('@')[0],
                    fontSize = 30.sp,
                    color = Color.White,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black, offset = Offset(2f, 2f), blurRadius = 4f
                        )
                    )
                )
            }

            Text(
                text = "Recommendations: ",
                fontSize = 22.sp,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black, offset = Offset(2f, 2f), blurRadius = 4f
                    )
                ),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
            )
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
                        views = song.views,
                        likes = song.likes,
                        playlistId = "null",
                        onClick = {
                            selectedSong = song
                            currentPlaylistId = -1
                        },
                        onLongClick = {

                        })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(100.dp)
//                .padding(16.dp)
//                .background(Color.Black, RoundedCornerShape(16.dp))
//                .align(Alignment.BottomCenter)
//        ) {
//            MusicPlayer(navController)
//        }

    }

    selectedSong?.let {
        title = it.title
        author = it.author
        musicID = it.id
        bitmap = it.thumbnail
        views = it.views
        likes = it.likes
//        Music(title = it.title, author = it.author, musicID = it.id, bitmap = it.thumbnail)
        navController.navigate(Screens.Music.screen)
        selectedSong = null
    }
}

data class Song(
    var id: String,
    val title: String,
    val author: String,
    val thumbnail: Bitmap?,
    val views: String,
    val likes: String,
    val playlistid: String
)
