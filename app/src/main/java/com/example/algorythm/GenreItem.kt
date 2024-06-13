package com.example.algorythm.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun GenreItem(
    genreName: String,
    isSelected: Boolean,
    onGenreSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(if (isSelected) MainTheme else Color.Gray, shape = RoundedCornerShape(50))
            .padding(horizontal = 32.dp, vertical = 12.dp)
            .clickable(onClick = {
                onGenreSelected()
            }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = genreName,
            color = Color.White,
            style = TextStyle(fontSize = 16.sp)
        )
    }
}