package com.nargok.sakemap.presentation.ui.screens

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nargok.sakemap.presentation.viewmodel.record.RecordRegisterViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleRecordScreen(
    viewModel: RecordRegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show success message with Snackbar
    LaunchedEffect(uiState.isSaveSuccessful) {
        if (uiState.isSaveSuccessful) {
            snackbarHostState.showSnackbar(
                message = "お酒の記録が保存されました！",
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccessMessage()
        }
    }

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
            value = uiState.drinkName,
            onValueChange = viewModel::updateDrinkName,
            label = { Text("銘柄名") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("例: 獺祭 純米大吟醸") },
            isError = uiState.drinkNameError != null,
            supportingText = uiState.drinkNameError?.let { 
                { Text(it, color = MaterialTheme.colorScheme.error) } 
            }
        )

        // Drink type dropdown
        ExposedDropdownMenuBox(
            expanded = uiState.showDrinkTypeDropdown,
            onExpandedChange = viewModel::setShowDrinkTypeDropdown
        ) {
            OutlinedTextField(
                value = uiState.selectedDrinkType,
                onValueChange = {},
                readOnly = true,
                label = { Text("お酒の種類") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                isError = uiState.drinkTypeError != null,
                supportingText = uiState.drinkTypeError?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) } 
                }
            )
            
            ExposedDropdownMenu(
                expanded = uiState.showDrinkTypeDropdown,
                onDismissRequest = { viewModel.setShowDrinkTypeDropdown(false) }
            ) {
                viewModel.getDrinkTypes().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = { viewModel.updateSelectedDrinkType(type) }
                    )
                }
            }
        }

        // Prefecture dropdown
        ExposedDropdownMenuBox(
            expanded = uiState.showPrefectureDropdown,
            onExpandedChange = viewModel::setShowPrefectureDropdown
        ) {
            OutlinedTextField(
                value = uiState.selectedPrefecture,
                onValueChange = {},
                readOnly = true,
                label = { Text("都道府県") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                isError = uiState.prefectureError != null,
                supportingText = uiState.prefectureError?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) } 
                }
            )
            
            ExposedDropdownMenu(
                expanded = uiState.showPrefectureDropdown,
                onDismissRequest = { viewModel.setShowPrefectureDropdown(false) }
            ) {
                viewModel.getPrefectures().forEach { prefecture ->
                    DropdownMenuItem(
                        text = { Text(prefecture) },
                        onClick = { viewModel.updateSelectedPrefecture(prefecture) }
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
                        imageVector = if (index < uiState.rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "${index + 1}つ星",
                        tint = if (index < uiState.rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                viewModel.updateRating(index + 1)
                            }
                    )
                }
            }
            
            uiState.ratingError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Date picker
        Column {
            OutlinedTextField(
                value = uiState.selectedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                onValueChange = {},
                readOnly = true,
                label = { Text("飲んだ日付") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.setShowDatePicker(true) }) {
                        Icon(Icons.Default.DateRange, contentDescription = "日付選択")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.dateError != null,
                supportingText = uiState.dateError?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) } 
                }
            )
        }

        // Memo field
        OutlinedTextField(
            value = uiState.description,
            onValueChange = viewModel::updateDescription,
            label = { Text("メモ") },
            placeholder = { Text("味の感想、飲んだ場所、一緒に食べた料理など...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            supportingText = { 
                Text(
                    text = "${uiState.description.length}/500文字",
                    color = if (uiState.description.length > 450) MaterialTheme.colorScheme.error 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = viewModel::saveRecord,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("保存", style = MaterialTheme.typography.titleMedium)
            }
        }

        }

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Date picker dialog
    if (uiState.showDatePicker) {
        DatePickerDialog(
            onDateSelected = { millis ->
                millis?.let { viewModel.updateSelectedDateFromMillis(it) }
                viewModel.setShowDatePicker(false)
            },
            onDismiss = { viewModel.setShowDatePicker(false) },
            initialSelectedDateMillis = viewModel.getSelectedDateInMillis()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    initialSelectedDateMillis: Long? = null
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleRecordScreenPreview() {
    MaterialTheme {
        // Note: Preview won't work with ViewModel injection
        // For preview, you would need to create a mock or use a different approach
    }
}