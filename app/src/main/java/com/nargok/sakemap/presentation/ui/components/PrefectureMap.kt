package com.nargok.sakemap.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nargok.sakemap.presentation.viewmodel.record.PrefectureStatistics

@Composable
fun PrefectureMap(
    prefectureStatistics: List<PrefectureStatistics>,
    onPrefectureClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allPrefectures = getAllPrefectures()
    val statisticsMap = prefectureStatistics.associateBy { it.name }

    Column(modifier = modifier) {
        // Legend
        PrefectureMapLegend()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Prefecture Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(allPrefectures) { prefecture ->
                val count = statisticsMap[prefecture]?.count ?: 0
                PrefectureItem(
                    name = prefecture,
                    count = count,
                    onClick = { onPrefectureClick(prefecture) }
                )
            }
        }
    }
}

@Composable
fun PrefectureItem(
    name: String,
    count: Int,
    onClick: () -> Unit
) {
    val backgroundColor = getPrefectureColor(count)
    val textColor = if (count == 0) MaterialTheme.colorScheme.onSurfaceVariant 
                   else MaterialTheme.colorScheme.onPrimary

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name.replace("県", "").replace("府", "").replace("都", "").replace("道", ""),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                if (count > 0) {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PrefectureMapLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(
            color = MaterialTheme.colorScheme.surfaceVariant,
            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            label = "未制覇"
        )
        LegendItem(
            color = Color(0xFF81C784), // Light blue-green
            textColor = MaterialTheme.colorScheme.onPrimary,
            label = "1-4件"
        )
        LegendItem(
            color = Color(0xFF1976D2), // Dark blue
            textColor = MaterialTheme.colorScheme.onPrimary,
            label = "5件以上"
        )
    }
}

@Composable
fun LegendItem(
    color: Color,
    textColor: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(2.dp)
                )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun getPrefectureColor(count: Int): Color {
    return when {
        count == 0 -> MaterialTheme.colorScheme.surfaceVariant
        count in 1..4 -> Color(0xFF81C784) // Light blue-green
        else -> Color(0xFF1976D2) // Dark blue
    }
}

private fun getAllPrefectures(): List<String> {
    return listOf(
        "北海道",
        "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県",
        "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県",
        "新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県",
        "静岡県", "愛知県", "三重県",
        "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県",
        "鳥取県", "島根県", "岡山県", "広島県", "山口県",
        "徳島県", "香川県", "愛媛県", "高知県",
        "福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県",
        "沖縄県"
    )
}