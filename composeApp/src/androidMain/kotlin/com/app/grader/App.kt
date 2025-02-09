package com.app.grader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxWidth()){
            LazyVerticalGrid(
                columns = GridCells.Adaptive(120.dp),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){
                items(movies.size, key = { movies[it].id }){ index ->
                    MovieItem(movie = movies[index])
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    Column {
        Box(
            modifier = Modifier.fillMaxWidth()
            .aspectRatio(2f/3)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colors.primary)
        )
        Text(
            text = movie.title,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            modifier = Modifier.padding(8.dp)
        )
    }
}