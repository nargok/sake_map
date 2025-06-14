package com.nargok.sakemap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nargok.sakemap.data.DrinkRecord
import com.nargok.sakemap.data.DrinkType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    drinkRecords: List<DrinkRecord> = mockDrinkRecords(),
    onDeleteRecord: (String) -> Unit = {}
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with filter and sort buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "お酒記録一覧",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row {
                IconButton(onClick = { showFilterSheet = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "フィルター")
                }
                IconButton(onClick = { showSortSheet = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "ソート")
                }
            }
        }

        // Records count
        Text(
            text = "${drinkRecords.size}件の記録",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Records list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(drinkRecords) { record ->
                DrinkRecordItem(
                    record = record,
                    onDelete = { onDeleteRecord(record.id) }
                )
            }
        }
    }

    // Filter bottom sheet (placeholder)
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            FilterBottomSheetContent(
                onDismiss = { showFilterSheet = false }
            )
        }
    }

    // Sort bottom sheet (placeholder)
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false }
        ) {
            SortBottomSheetContent(
                onDismiss = { showSortSheet = false }
            )
        }
    }
}

@Composable
fun DrinkRecordItem(
    record: DrinkRecord,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📷",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Drink info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = record.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${getDrinkTypeText(record.type)} • ${record.prefecture}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Rating stars
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < record.rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index < record.rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Text(
                    text = record.drinkDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun FilterBottomSheetContent(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "フィルター",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Placeholder filter options
        Text("都道府県フィルター")
        Text("お酒種類フィルター")
        Text("評価フィルター")
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("適用")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SortBottomSheetContent(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "ソート",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Placeholder sort options
        Text("日付順")
        Text("評価順")
        Text("名前順")
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("適用")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun getDrinkTypeText(type: DrinkType): String {
    return when (type) {
        DrinkType.SAKE -> "日本酒"
        DrinkType.BEER -> "ビール"
        DrinkType.WHISKEY -> "ウイスキー"
        DrinkType.SHOCHU -> "焼酎"
        DrinkType.WINE -> "ワイン"
    }
}

// Mock data for preview
fun mockDrinkRecords(): List<DrinkRecord> {
    return listOf(
        DrinkRecord(
            id = "1",
            name = "獺祭 純米大吟醸",
            type = DrinkType.SAKE,
            prefecture = "山口県",
            rating = 5,
            photoPath = null,
            drinkDate = LocalDate.of(2024, 1, 15),
            createdAt = LocalDateTime.now()
        ),
        DrinkRecord(
            id = "2",
            name = "プレミアムモルツ",
            type = DrinkType.BEER,
            prefecture = "東京都",
            rating = 4,
            photoPath = null,
            drinkDate = LocalDate.of(2024, 1, 10),
            createdAt = LocalDateTime.now()
        ),
        DrinkRecord(
            id = "3",
            name = "山崎 12年",
            type = DrinkType.WHISKEY,
            prefecture = "大阪府",
            rating = 5,
            photoPath = null,
            drinkDate = LocalDate.of(2023, 12, 25),
            createdAt = LocalDateTime.now()
        ),
        DrinkRecord(
            id = "4",
            name = "魔王",
            type = DrinkType.SHOCHU,
            prefecture = "鹿児島県",
            rating = 4,
            photoPath = null,
            drinkDate = LocalDate.of(2023, 11, 20),
            createdAt = LocalDateTime.now()
        ),
        DrinkRecord(
            id = "5",
            name = "シャトー・マルゴー",
            type = DrinkType.WINE,
            prefecture = "山梨県",
            rating = 5,
            photoPath = null,
            drinkDate = LocalDate.of(2023, 10, 15),
            createdAt = LocalDateTime.now()
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    MaterialTheme {
        ListScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DrinkRecordItemPreview() {
    MaterialTheme {
        DrinkRecordItem(
            record = DrinkRecord(
                id = "1",
                name = "獺祭 純米大吟醸",
                type = DrinkType.SAKE,
                prefecture = "山口県",
                rating = 5,
                photoPath = null,
                drinkDate = LocalDate.of(2024, 1, 15),
                createdAt = LocalDateTime.now()
            ),
            onDelete = {}
        )
    }
}