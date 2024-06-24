package com.example.algorythm

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale

@Composable
fun PlaylistItem(
    bitmap: Bitmap?,
    id: String,
    placeholderResId: Int,
    title: String,
    onClick: () -> Unit,
    onButtonClick: () -> Unit,
    showDeleteOption: Boolean
) {
    val expanded = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val shareCode = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(70.dp)
                            .shadow(
                                elevation = 15.dp,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                    )
                } ?: Image(
                    painter = painterResource(id = placeholderResId),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(
                            elevation = 15.dp,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            Box {
                IconButton(
                    onClick = { expanded.value = true }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ui_3_dots),
                        contentDescription = "More options",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    offset = DpOffset(x = (-40).dp, y = 0.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Share") },
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                                val jwt = sharedPref.getString("JWT", "") ?: ""
                                val code = API.sharePlaylist(id, jwt)
                                withContext(Dispatchers.Main) {
                                    shareCode.value = code
                                    showDialog.value = true
                                    expanded.value = false
                                }
                            }
                        }
                    )
                    if (showDeleteOption) DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded.value = false
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                                    val jwt = sharedPref.getString("JWT", "") ?: ""
                                    println("JWT $jwt")
                                    API.deletePlaylist(id, jwt)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Share Code") },
            text = { Text(text = shareCode.value) },
            confirmButton = {
                TextButton(onClick = {
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip =
                        ClipData.newPlainText("Playlist Share Code", shareCode.value)
                    clipboard.setPrimaryClip(clip)
                    showDialog.value = false
                }) {
                    Text("Copy")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Close")
                }
            }
        )
    }
}
