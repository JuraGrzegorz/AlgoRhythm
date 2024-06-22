package com.example.algorythm

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = LocalContext.current as Activity


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
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                } ?: Image(
                    painter = painterResource(id = placeholderResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(2.dp))
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
                            expanded.value = false
                            // Handle Share action
                        }
                    )
                    if (showDeleteOption) DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded.value = false
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                                    var jwt = sharedPref.getString("JWT","") ?:""
                                    println("JWT " + jwt)
                                    API.deletePlaylist(id, jwt)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
