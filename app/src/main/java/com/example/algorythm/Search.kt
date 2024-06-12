import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorythm.API.getMusicByTitle
import com.example.algorythm.Music
import com.example.algorythm.SongItem
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.example.algorythm.ui.theme.MainTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

private const val PREFS_NAME = "recent_searches"
private const val PREFS_KEY = "recent_songs"
private const val recentSearchAmount = 10
private const val songAmount = 10

fun saveSong(context: Context, song: Song) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = prefs.edit()
    val songsJson = prefs.getString(PREFS_KEY, "[]")
    val songsArray = JSONArray(songsJson)

    for (i in 0 until songsArray.length()) {
        val existingSong = songsArray.getJSONObject(i)
        if (existingSong.getString("id") == song.id) {
            songsArray.remove(i)
            break
        }
    }

    if (songsArray.length() >= recentSearchAmount) {
        songsArray.remove(0)
    }

    val songJson = JSONObject().apply {
        put("id", song.id)
        put("title", song.title)
        put("author", song.author)
        put("thumbnailData", Base64.encodeToString(song.thumbnail?.toByteArray(), Base64.DEFAULT))
    }

    songsArray.put(songJson)
    editor.putString(PREFS_KEY, songsArray.toString())
    editor.apply()
}

fun getSavedSongs(context: Context): List<Song> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val songsJson = prefs.getString(PREFS_KEY, "[]")
    val songsArray = JSONArray(songsJson)
    val songList = mutableListOf<Song>()

    for (i in 0 until songsArray.length()) {
        val obj = songsArray.getJSONObject(i)
        val id = obj.getString("id")
        val title = obj.getString("title")
        val author = obj.getString("author")
        val thumbnailData = obj.getString("thumbnailData")
        val imageBytes = Base64.decode(thumbnailData, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        songList.add(Song(id, title, author, bitmap))
    }

    return songList.asReversed()
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

@Composable
fun Search() {
    val activity = LocalContext.current as Activity
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Black
    )

    var searchText by remember { mutableStateOf("") }
    var songs by remember { mutableStateOf(listOf<Song>()) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var recentSongs by remember { mutableStateOf(listOf<Song>()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(searchText) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                if (searchText.isNotEmpty()) {
                    var jwt = ""
                    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                    jwt = sharedPref.getString("JWT","") ?:""
                    val data: String = getMusicByTitle(searchText, songAmount,jwt)
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
                } else {
                    songs = emptyList()
                    recentSongs = getSavedSongs(context)
                }
            } catch (_: Exception) {
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDarkGray)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = {
                    Text(text = "What do you want to listen to?", color = Color.Gray)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MainTheme,
                    unfocusedBorderColor = Color.White,
                    cursorColor = MainTheme,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchText.isEmpty()) {
                Text(
                    text = "Recent Searches",
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(recentSongs) { song ->
                        SongItem(bitmap = song.thumbnail,
                            title = song.title,
                            author = song.author,
                            onClick = {
                                keyboardController?.hide()
                                saveSong(context, song)
                                selectedSong = song
                            })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else if (songs.isNotEmpty()) {
                Text(
                    text = "Search Results",
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
                                keyboardController?.hide()
                                saveSong(context, song)
                                selectedSong = song
                            })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
    selectedSong?.let {
        Music(title = it.title, author = it.author, musicID = it.id, bitmap = it.thumbnail)
    }
}
