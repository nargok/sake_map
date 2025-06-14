package com.nargok.sakemap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleRecordScreen() {
    var drinkName by remember { mutableStateOf("") }
    var selectedDrinkType by remember { mutableStateOf("") }
    var selectedPrefecture by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var description by remember { mutableStateOf("") }
    var showDrinkTypeDropdown by remember { mutableStateOf(false) }
    var showPrefectureDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val drinkTypes = listOf("日本酒", "ビール", "ウイスキー", "焼酎", "ワイン")
    val prefectures = listOf(
        "北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県",
        "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県",
        "新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県",
        "静岡県", "愛知県", "三重県", "滋賀県", "京都府", "大阪府", "兵庫県",
        "奈良県", "和歌山県", "鳥取県", "島根県", "岡山県", "広島県", "山口県",
        "徳島県", "香川県", "愛媛県", "高知県", "福岡県", "佐賀県", "長崎県",
        "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "お酒を記録",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Photo capture area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(12.dp)
                )
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "カメラ",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "写真を撮影",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "タップしてカメラを起動",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Drink name input
        OutlinedTextField(
            value = drinkName,
            onValueChange = { drinkName = it },
            label = { Text("銘柄名") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("例: 獺祭 純米大吟醸") }
        )

        // Drink type dropdown
        ExposedDropdownMenuBox(
            expanded = showDrinkTypeDropdown,
            onExpandedChange = { showDrinkTypeDropdown = it }
        ) {
            OutlinedTextField(
                value = selectedDrinkType,
                onValueChange = {},
                readOnly = true,
                label = { Text("お酒の種類") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = showDrinkTypeDropdown,
                onDismissRequest = { showDrinkTypeDropdown = false }
            ) {
                drinkTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedDrinkType = type
                            showDrinkTypeDropdown = false
                        }
                    )
                }
            }
        }

        // Prefecture dropdown
        ExposedDropdownMenuBox(
            expanded = showPrefectureDropdown,
            onExpandedChange = { showPrefectureDropdown = it }
        ) {
            OutlinedTextField(
                value = selectedPrefecture,
                onValueChange = {},
                readOnly = true,
                label = { Text("都道府県") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = showPrefectureDropdown,
                onDismissRequest = { showPrefectureDropdown = false }
            ) {
                prefectures.forEach { prefecture ->
                    DropdownMenuItem(
                        text = { Text(prefecture) },
                        onClick = {
                            selectedPrefecture = prefecture
                            showPrefectureDropdown = false
                        }
                    )
                }
            }
        }

        // Rating selection
        Column {
            Text(
                text = "評価",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "${index + 1}つ星",
                        tint = if (index < rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                rating = index + 1
                            }
                    )
                }
            }
        }

        // Date picker
        OutlinedTextField(
            value = selectedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
            onValueChange = {},
            readOnly = true,
            label = { Text("飲んだ日付") },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "日付選択")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Memo field
        OutlinedTextField(
            value = description,
            onValueChange = { newValue ->
                if (newValue.length <= 500) {
                    description = newValue
                }
            },
            label = { Text("メモ") },
            placeholder = { Text("味の感想、飲んだ場所、一緒に食べた料理など...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            supportingText = { 
                Text(
                    text = "${description.length}/500文字",
                    color = if (description.length > 450) MaterialTheme.colorScheme.error 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = { 
                // TODO: Save logic
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("保存", style = MaterialTheme.typography.titleMedium)
        }
    }

    // Date picker dialog (placeholder)
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("日付を選択") },
            text = { Text("実際の実装では DatePickerDialog を使用します") },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleRecordScreenPreview() {
    MaterialTheme {
        SimpleRecordScreen()
    }
}