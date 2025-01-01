package com.sss.constructionroster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Button


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConstructionRosterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var selectedImage by remember { mutableStateOf<ImageItem?>(null) }
                    var selectedMonthImage by remember { mutableStateOf<MonthImageSelection?>(null) }
                    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        when {
                            selectedDate != null -> {
                                DateDetailsScreen(
                                    date = selectedDate!!,
                                    amountPerDay = selectedMonthImage?.amountPerDay ?: "",
                                    monthImage = selectedMonthImage!!,
                                    onDismiss = { selectedDate = null },
                                    onSubmit = { dateDetails ->
                                        selectedMonthImage = selectedMonthImage?.copy(
                                            dayEntries = selectedMonthImage?.dayEntries?.apply {
                                                put(dateDetails.date.dayOfMonth, DayEntry(
                                                    date = dateDetails.date,
                                                    amountPaid = dateDetails.amountPaid,
                                                    isAbsent = dateDetails.isAbsent
                                                ))
                                            } ?: mutableMapOf()
                                        )
                                        selectedDate = null
                                    }
                                )
                            }
                            selectedMonthImage != null -> {
                                MonthImageScreen(
                                    monthImage = selectedMonthImage!!,
                                    onDismiss = { selectedMonthImage = null },
                                    onDateSelected = { date -> selectedDate = date },
                                    onAmountPerDayChange = { amount ->
                                        selectedMonthImage = selectedMonthImage?.copy(
                                            amountPerDay = amount
                                        )
                                    }
                                )
                            }
                            else -> {
                                ImageGrid(
                                    modifier = Modifier.weight(1f),
                                    onImageSelected = { imageItem ->
                                        selectedImage = imageItem
                                    }
                                )

                                selectedImage?.let { image ->
                                    SelectedImageDisplay(
                                        imageRes = image.imageRes,
                                        title = image.title,
                                        onDismiss = { selectedImage = null }
                                    )
                                    MonthCalendar(
                                        onMonthSelected = { month ->
                                            selectedMonthImage = MonthImageSelection(
                                                month = month,
                                                imageItem = image,
                                                amountPerDay = ""
                                            )
                                        }
                                    )
                                }
                            }
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

data class MonthImageSelection(
    val month: Month,
    val imageItem: ImageItem,
    val amountPerDay: String = "",
    val dayEntries: MutableMap<Int, DayEntry> = mutableMapOf()
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
            modifier = Modifier.padding(16.dp)
        ) {
            // Add back button row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun MonthCalendar(
    onMonthSelected: (Month) -> Unit
) {
    var selectedMonth by remember { mutableStateOf<Month?>(null) }
    val currentYear = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .year

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Color(0xFF000000), // Light green background
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
                    onMonthClick = { 
                        selectedMonth = month
                        onMonthSelected(month)
                    }
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

@Composable
fun MonthImageScreen(
    monthImage: MonthImageSelection,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onAmountPerDayChange: (String) -> Unit
) {
    var amountPerDay by remember(monthImage.amountPerDay) { 
        mutableStateOf(monthImage.amountPerDay) 
    }
    val currentYear = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .year

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "${monthImage.month.name.take(3)} ${monthImage.month.number.toString().padStart(2, '0')} $currentYear",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // This ensures the column takes remaining space
                .verticalScroll(rememberScrollState())
        ) {
            // Image Card with reduced height
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = monthImage.imageItem.imageRes),
                    contentDescription = monthImage.imageItem.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Amount per day row with compact layout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Amount per day",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    OutlinedTextField(
                        value = amountPerDay,
                        onValueChange = { newValue -> 
                            amountPerDay = newValue
                            onAmountPerDayChange(newValue)
                        },
                        modifier = Modifier.width(140.dp),
                        placeholder = { Text("Enter amount", fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            // Calendar Card with optimized spacing
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Days of week header with reduced spacing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                            Text(
                                text = day,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Calendar grid
                    MonthDaysGrid(
                        month = monthImage.month,
                        year = currentYear,
                        monthImage = monthImage,
                        onDateSelected = onDateSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthDaysGrid(
    month: Month,
    year: Int,
    monthImage: MonthImageSelection,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = LocalDate(year, month.number, 1)
    val daysInMonth = when (month.number) {
        2 -> if (isLeapYear(year)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false,
        modifier = Modifier.height(((daysInMonth + firstDayOfWeek + 6) / 7 * 50).dp)
    ) {
        // Empty cells before first day
        items(firstDayOfWeek) {
            Box(modifier = Modifier.size(50.dp))
        }
        
        // Days of the month
        items(daysInMonth) { day ->
            val dayOfMonth = day + 1
            val dayEntry = monthImage.dayEntries[dayOfMonth]
            DayCell(
                day = dayOfMonth,
                isToday = isToday(year, month.number, dayOfMonth),
                dayEntry = dayEntry,
                onClick = { onDateSelected(LocalDate(year, month.number, dayOfMonth)) }
            )
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isToday: Boolean,
    dayEntry: DayEntry?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(50.dp)
            .clickable(onClick = onClick),
        border = BorderStroke(
            width = if (isToday) 2.dp else 1.dp,
            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                dayEntry?.isAbsent == true -> MaterialTheme.colorScheme.errorContainer
                dayEntry != null -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            )
            if (dayEntry != null && !dayEntry.isAbsent) {
                Text(
                    text = "₹${dayEntry.amountPaid}",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun isToday(year: Int, month: Int, day: Int): Boolean {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return today.year == year && today.monthNumber == month && today.dayOfMonth == day
}

// Add this helper function to check for leap years
private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

// Add this data class for date details
data class DateDetails(
    val date: LocalDate,
    val amountPerDay: String,
    val amountPaid: String = "",
    val isAbsent: Boolean = false
)

// Add this data class to track daily entries
data class DayEntry(
    val date: LocalDate,
    val amountPaid: String,
    val isAbsent: Boolean
)

// Update DateDetailsScreen to show total calculations
@Composable
fun DateDetailsScreen(
    date: LocalDate,
    amountPerDay: String,
    monthImage: MonthImageSelection,
    onDismiss: () -> Unit,
    onSubmit: (DateDetails) -> Unit
) {
    var amountPaid by remember { mutableStateOf("") }
    var isAbsent by remember { mutableStateOf(false) }

    // Calculate totals
    val daysInMonth = when (date.monthNumber) {
        2 -> if (isLeapYear(date.year)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    
    val totalPossibleAmount = amountPerDay.toIntOrNull()?.times(daysInMonth) ?: 0
    val totalPaidAmount = monthImage.dayEntries.values.sumOf { it.amountPaid.toIntOrNull() ?: 0 }
    val absentDaysAmount = monthImage.dayEntries.values.count { it.isAbsent } * (amountPerDay.toIntOrNull() ?: 0)
    val remainingAmount = totalPossibleAmount - totalPaidAmount - absentDaysAmount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "${date.month.name} ${date.dayOfMonth}, ${date.year}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Amount per day with Total
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Amount per day (read-only)
                OutlinedTextField(
                    value = amountPerDay,
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text("Amount per day") },
                    readOnly = true,
                    enabled = false
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Total calculations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Month Total: ₹$totalPossibleAmount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Paid: ₹$totalPaidAmount",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Absent Days: ${monthImage.dayEntries.values.count { it.isAbsent }}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Remaining: ₹$remainingAmount",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // Amount Paid
        OutlinedTextField(
            value = amountPaid,
            onValueChange = { amountPaid = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text("Amount Paid") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )

        // Absent Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isAbsent,
                onCheckedChange = { isAbsent = it }
            )
            Text(
                text = "Absent",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Submit Button
        Button(
            onClick = {
                onSubmit(
                    DateDetails(
                        date = date,
                        amountPerDay = amountPerDay,
                        amountPaid = amountPaid,
                        isAbsent = isAbsent
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Submit")
        }
    }
}