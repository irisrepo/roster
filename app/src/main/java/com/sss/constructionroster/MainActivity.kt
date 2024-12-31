package com.sss.constructionroster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sss.constructionroster.ui.theme.ConstructionRosterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConstructionRosterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImageGrid(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class ImageItem(
    val imageRes: Int,
    val title: String
)

val sampleImages = listOf(
    ImageItem(R.drawable.sample_image1, "Sample 1"),
    ImageItem(R.drawable.sample_image2, "Sample 2"),
    ImageItem(R.drawable.sample_image3, "Sample 3"),
    ImageItem(R.drawable.sample_image4, "Sample 4"),
    ImageItem(R.drawable.sample_image5, "Sample 5"),
    ImageItem(R.drawable.sample_image6, "Sample 6")
)

@Composable
fun ImageGrid(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(sampleImages) { item ->
            ImageCard(item)
        }
    }
}

@Composable
fun ImageCard(item: ImageItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}