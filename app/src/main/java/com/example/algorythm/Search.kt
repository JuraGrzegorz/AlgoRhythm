import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.navigation.NavController
import com.example.algorythm.API.getGenres
import com.example.algorythm.API.getMusicByTitle
import com.example.algorythm.Music
import com.example.algorythm.Screens
import com.example.algorythm.SongItem
import com.example.algorythm.ui.theme.BackgroundDarkGray
import com.example.algorythm.ui.theme.GenreItem
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
        put("views", song.views)
        put("likes", song.likes)
    }

    songsArray.put(songJson)
    editor.putString(PREFS_KEY, songsArray.toString())
    editor.apply()
}

fun getSavedSongs(context: Context): List<Song> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val songsJson = prefs.getString(PREFS_KEY, "[]")
        val songsArray = JSONArray(songsJson)
        Log.e("getSavedSongs", songsArray.toString())
        val songList = mutableListOf<Song>()

        for (i in 0 until songsArray.length()) {
            val obj = songsArray.getJSONObject(i)
            val id = obj.getString("id")
            val title = obj.getString("title")
            val author = obj.getString("author")
            val thumbnailData = obj.getString("thumbnailData")
            val views = obj.getString("views")
            val likes = obj.getString("likes")
            val imageBytes = Base64.decode(thumbnailData, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            songList.add(Song(id, title, author, bitmap, views, likes, "null"))
        }

        println("Effekt" + songList.size)
        return songList.asReversed()
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

@Composable
fun Search(navController: NavController) {
    val activity = LocalContext.current as Activity
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Black
    )

    var searchText by remember { mutableStateOf("") }
    var songs by remember { mutableStateOf(listOf<Song>()) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var recentSongs by remember { mutableStateOf(listOf<Song>()) }
    var currentGenre by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) } // State for loading
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var Genries by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val genresJson = getGenres()
                val jsonArray = JSONArray(genresJson)
                Genries = List(jsonArray.length()) { index ->
                    jsonArray.getString(index)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    LaunchedEffect(searchText) {
        isLoading = true // Set loading to true when search text changes
        coroutineScope.launch(Dispatchers.IO) {
            if (searchText.isNotEmpty()) {
                try {
                    var jwt = ""
                    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                    jwt = sharedPref.getString("JWT","") ?:""
                    val data: String = getMusicByTitle(searchText, songAmount, jwt, currentGenre)
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
                } catch (e: Exception) {
                    println("EFFEKT SEARCH" + e)
                } finally {
                    isLoading = false // Set loading to false after search results are fetched
                }
            } else {
                songs = emptyList()
                recentSongs = getSavedSongs(context)
                isLoading = false // Set loading to false after recent songs are fetched
            }
        }
    }

    LaunchedEffect(currentGenre) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                if (!currentGenre.isNullOrEmpty()) {
                    var jwt = ""
                    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                    jwt = sharedPref.getString("JWT","") ?:""
                    val data: String = getMusicByTitle(searchText, songAmount, jwt, currentGenre)
                    val arr = JSONArray(data)
                    val songList = mutableListOf<Song>()
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        val id = obj.getString("id")
                        val title = obj.getString("title")
                        val author = obj.getString("artistName")
                        val views = obj.getString("views")
                        val likes = obj.getString("likes")
                        val thumbnailData = obj.getString("thumbnailData")
                        val imageBytes = Base64.decode(thumbnailData, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        songList.add(Song(id, title, author, bitmap, views,likes, "null" ))
                    }
                    songs = songList
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
                onValueChange = {
                    searchText = it
                    isLoading = true // Trigger loading state immediately after text change
                },
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

            Spacer(modifier = Modifier.height(16.dp))

            if (searchText.isEmpty()) {
                currentGenre = null

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
                            views = song.views,
                            likes = song.likes,
                            playlistId = "null",
                            onClick = {
                                keyboardController?.hide()
                                saveSong(context, song)
                                selectedSong = song
                            },
                            onLongClick = {

                            })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(Genries) { genre ->
                        GenreItem(
                            genreName = genre,
                            isSelected = genre == currentGenre,
                            onGenreSelected = {
                                currentGenre = if (currentGenre == genre) null else genre
                            }
                        )
                    }
                }

                if (isLoading) {
                    // Display a loading indicator or empty state while loading
                    Text(
                        text = "Loading...",
                        fontSize = 22.sp,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
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
                                views = song.views,
                                likes = song.likes,
                                playlistId = "null",

                                onClick = {
                                    keyboardController?.hide()
                                    saveSong(context, song)
                                    selectedSong = song
                                },
                                onLongClick = {

                                })
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    selectedSong?.let {
        title = it.title
        author = it.author
        musicID = it.id
        bitmap = it.thumbnail
        views = it.views
        likes = it.likes
        navController.navigate(Screens.Music.screen)
        selectedSong = null
    }
}
