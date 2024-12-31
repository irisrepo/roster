package com.sss.constructionroster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sss.constructionroster.ui.theme.ConstructionRosterTheme

import kotlinx.datetime.*
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.shape.RoundedCornerShape


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConstructionRosterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var selectedImage by remember { mutableStateOf<ImageItem?>(null) }
                    
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        ImageGrid(
                            modifier = Modifier.weight(1f),
                            onImageSelected = { imageItem ->
                                selectedImage = imageItem
                            }
                        )
                        
                        // Only show selected image and calendar when an image is selected
                        selectedImage?.let { image ->
                            SelectedImageDisplay(
                                imageRes = image.imageRes,
                                title = image.title,
                                onDismiss = { selectedImage = null }
                            )
                            // Calendar is only shown when an image is selected
                            MonthCalendar()
                        }
                    }
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

data class SelectedImage(
    val imageRes: Int,
    val title: String,
    val isVisible: Boolean
)

@Composable
fun ImageGrid(
    modifier: Modifier = Modifier,
    onImageSelected: (ImageItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(sampleImages) { item ->
            ImageCard(
                item = item,
                onImageClick = { onImageSelected(item) }
            )
        }
    }
}

@Composable
fun ImageCard(
    item: ImageItem,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onImageClick)
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

@Composable
fun SelectedImageDisplay(
    imageRes: Int,
    title: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun MonthCalendar() {
    var selectedMonth by remember { mutableStateOf<Month?>(null) }
    val currentYear = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .year
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Color(0xFFE8F5E9), // Light green background
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Calendar $currentYear",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32) // Darker green for title
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(Month.values().toList()) { month ->
                MonthCard(
                    month = month,
                    year = currentYear,
                    isSelected = month == selectedMonth,
                    onMonthClick = { selectedMonth = month }
                )
            }
        }
    }
}

@Composable
fun MonthCard(
    month: Month,
    year: Int,
    isSelected: Boolean,
    onMonthClick: () -> Unit
) {
    val date = LocalDate(year, month.number, 1)
    val backgroundColor = if (isSelected) {
        Color(0xFF81C784) // Selected green
    } else {
        Color.White
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onMonthClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF2E7D32) else Color(0xFFAED581),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = month.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else Color(0xFF33691E)
                )
            )
            Text(
                text = "${month.number}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = if (isSelected) Color.White else Color(0xFF33691E)
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "Starts ${date.dayOfWeek.name.take(3)}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else Color(0xFF558B2F)
                )
            )
        }
    }
}