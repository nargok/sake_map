package com.nargok.sakemap.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.nargok.sakemap.domain.model.DrinkRecord
import com.nargok.sakemap.presentation.viewmodel.record.DrinkRecordListViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class SimpleDrinkRecord(
    val id: String,
    val name: String,
    val type: String,
    val prefecture: String,
    val rating: Int,
    val drinkDate: LocalDate,
    val memo: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleListScreen(
    onNavigateToEdit: (String) -> Unit = {},
    viewModel: DrinkRecordListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error message with Snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMessage ->
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Long
            )
            viewModel.clearErrorMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with refresh button
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
                
                IconButton(
                    onClick = { viewModel.loadDrinkRecords() },
                    enabled = !uiState.isLoading
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "更新"
                    )
                }
            }

            // Records count
            Text(
                text = "${uiState.drinkRecords.size}件の記録",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Records list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.drinkRecords) { record ->
                        DrinkRecordItem(
                            record = record,
                            onEdit = { onNavigateToEdit(record.id.value) },
                            onDelete = { viewModel.showDeleteDialog(record) }
                        )
                    }
                }
            }
        }

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Delete confirmation dialog
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text("削除確認") },
            text = { Text("この記録を削除しますか？") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteRecord() }
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDeleteDialog() }
                ) {
                    Text("キャンセル")
                }
            }
        )
    }
}

@Composable
fun DrinkRecordItem(
    record: DrinkRecord,
    onEdit: () -> Unit,
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
            // Photo or placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (record.photoPath != null) {
                    Image(
                        painter = rememberAsyncImagePainter(record.photoPath.toUri()),
                        contentDescription = "お酒の写真",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "📷",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
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
                    text = buildString {
                        if (record.manufacturer != null) {
                            append("${record.manufacturer} • ")
                        }
                        append("${record.type.displayName} • ${record.prefecture.kanji}")
                    },
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
                
                record.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Edit and Delete buttons
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "編集",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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
}

@Composable
fun SimpleDrinkRecordItem(
    record: SimpleDrinkRecord,
    onEdit: () -> Unit,
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
                    text = "${record.type} • ${record.prefecture}",
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
                
                if (record.memo.isNotBlank()) {
                    Text(
                        text = record.memo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Edit and Delete buttons
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "編集",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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
}

fun mockSimpleDrinkRecords(): List<SimpleDrinkRecord> {
    return listOf(
        SimpleDrinkRecord(
            id = "1",
            name = "獺祭 純米大吟醸",
            type = "日本酒",
            prefecture = "山口県",
            rating = 5,
            drinkDate = LocalDate.of(2024, 1, 15),
            memo = "とても上品で華やかな香り。フルーティーで飲みやすい。"
        ),
        SimpleDrinkRecord(
            id = "2",
            name = "プレミアムモルツ",
            type = "ビール",
            prefecture = "東京都",
            rating = 4,
            drinkDate = LocalDate.of(2024, 1, 10),
            memo = "すっきりとした味わいでキレが良い。"
        ),
        SimpleDrinkRecord(
            id = "3",
            name = "山崎 12年",
            type = "ウイスキー",
            prefecture = "大阪府",
            rating = 5,
            drinkDate = LocalDate.of(2023, 12, 25),
            memo = "深いコクと芳醇な香り。記念日にぴったり。"
        ),
        SimpleDrinkRecord(
            id = "4",
            name = "魔王",
            type = "焼酎",
            prefecture = "鹿児島県",
            rating = 4,
            drinkDate = LocalDate.of(2023, 11, 20),
            memo = "クセが少なく飲みやすい芋焼酎。"
        ),
        SimpleDrinkRecord(
            id = "5",
            name = "シャトー・マルゴー",
            type = "ワイン",
            prefecture = "山梨県",
            rating = 5,
            drinkDate = LocalDate.of(2023, 10, 15),
            memo = "エレガントで複雑な味わい。特別な夜に。"
        ),
        SimpleDrinkRecord(
            id = "6",
            name = "グレイグース",
            type = "ウォッカ",
            prefecture = "東京都",
            rating = 4,
            drinkDate = LocalDate.of(2023, 9, 10),
            memo = "クリアでスムースな味わい。カクテルベースに最適。"
        ),
        SimpleDrinkRecord(
            id = "7",
            name = "ボンベイ・サファイア",
            type = "ジン",
            prefecture = "神奈川県",
            rating = 4,
            drinkDate = LocalDate.of(2023, 8, 5),
            memo = "ボタニカルな香りが特徴的。ジントニックで楽しんだ。"
        ),
        SimpleDrinkRecord(
            id = "8",
            name = "バカルディ",
            type = "ラム",
            prefecture = "沖縄県",
            rating = 3,
            drinkDate = LocalDate.of(2023, 7, 20),
            memo = "軽やかで飲みやすい。夏のパーティーで。"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SimpleListScreenPreview() {
    MaterialTheme {
        // Preview with mock data since we can't inject ViewModel
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with refresh button
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
                    
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "更新"
                        )
                    }
                }
                
                Text(
                    text = "3件の記録",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mockSimpleDrinkRecords().take(3)) { record ->
                        SimpleDrinkRecordItem(
                            record = record,
                            onEdit = {},
                            onDelete = {}
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleDrinkRecordItemPreview() {
    MaterialTheme {
        SimpleDrinkRecordItem(
            record = SimpleDrinkRecord(
                id = "1",
                name = "獺祭 純米大吟醸",
                type = "日本酒",
                prefecture = "山口県",
                rating = 5,
                drinkDate = LocalDate.of(2024, 1, 15),
                memo = "とても上品で華やかな香り。"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}