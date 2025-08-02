package com.nargok.sakemap.presentation.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.nargok.sakemap.presentation.viewmodel.record.RecordEditViewModel
import com.nargok.sakemap.presentation.ui.components.PhotoPickerDialog
import java.io.File
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleEditScreen(
    recordId: String,
    onNavigateBack: () -> Unit,
    viewModel: RecordEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Load record when the screen is first displayed
    LaunchedEffect(recordId) {
        viewModel.loadRecord(recordId)
    }

    // Create a temporary file for camera capture
    val tempImageFile = remember {
        File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
    }
    
    val tempImageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempImageFile
        )
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.updateSelectedPhoto(tempImageUri)
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.updateSelectedPhoto(it) }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(tempImageUri)
        }
    }

    // Show success message with Snackbar
    LaunchedEffect(uiState.isUpdateSuccessful) {
        if (uiState.isUpdateSuccessful) {
            snackbarHostState.showSnackbar(
                message = "お酒の記録が更新されました！",
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccessMessage()
            onNavigateBack()
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

    // Date picker
    if (uiState.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = viewModel.getSelectedDateInMillis()
        )
        
        DatePickerDialog(
            onDateSelected = { millis ->
                millis?.let { viewModel.updateSelectedDateFromMillis(it) }
                viewModel.setShowDatePicker(false)
            },
            onDismiss = { viewModel.setShowDatePicker(false) }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Photo picker dialog
    if (uiState.showPhotoPickerDialog) {
        PhotoPickerDialog(
            onCameraSelected = {
                viewModel.hidePhotoPickerDialog()
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onGallerySelected = {
                viewModel.hidePhotoPickerDialog()
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onDismiss = { viewModel.hidePhotoPickerDialog() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("お酒記録の編集") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoadingRecord) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Photo section
                PhotoSection(
                    selectedPhotoUri = uiState.selectedPhotoUri,
                    onPhotoClick = { viewModel.showPhotoPickerDialog() },
                    onPhotoRemove = { viewModel.removeSelectedPhoto() }
                )

                // Drink name input
                OutlinedTextField(
                    value = uiState.drinkName,
                    onValueChange = viewModel::updateDrinkName,
                    label = { Text("銘柄名 *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.drinkNameError != null,
                    supportingText = uiState.drinkNameError?.let { { Text(it) } }
                )

                // Manufacturer input
                OutlinedTextField(
                    value = uiState.manufacturer,
                    onValueChange = viewModel::updateManufacturer,
                    label = { Text("製造元") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.manufacturerError != null,
                    supportingText = uiState.manufacturerError?.let { { Text(it) } }
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
                        label = { Text("お酒の種類 *") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        isError = uiState.drinkTypeError != null,
                        supportingText = uiState.drinkTypeError?.let { { Text(it) } }
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
                        label = { Text("都道府県 *") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        isError = uiState.prefectureError != null,
                        supportingText = uiState.prefectureError?.let { { Text(it) } }
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

                // Rating section
                RatingSection(
                    rating = uiState.rating,
                    onRatingChange = viewModel::updateRating,
                    error = uiState.ratingError
                )

                // Date picker
                OutlinedTextField(
                    value = uiState.selectedDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("飲んだ日付") },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.setShowDatePicker(true) }) {
                            Icon(Icons.Default.DateRange, "")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.dateError != null,
                    supportingText = uiState.dateError?.let { { Text(it) } }
                )

                // Description input
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::updateDescription,
                    label = { Text("メモ") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    supportingText = { 
                        Text(
                            "${uiState.description.length}/500",
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )

                // Update button
                Button(
                    onClick = { viewModel.updateRecord() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("更新する")
                }
            }
        }
    }
}

@Composable
private fun PhotoSection(
    selectedPhotoUri: Uri?,
    onPhotoClick: () -> Unit,
    onPhotoRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "写真",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onPhotoClick() },
                contentAlignment = Alignment.Center
            ) {
                if (selectedPhotoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedPhotoUri),
                        contentDescription = "選択された写真",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Remove button
                    IconButton(
                        onClick = onPhotoRemove,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                RoundedCornerShape(16.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "写真を削除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📷",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            text = "タップして写真を選択",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingSection(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    error: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "評価 *",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    IconButton(
                        onClick = { onRatingChange(starIndex) }
                    ) {
                        Icon(
                            imageVector = if (starIndex <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "${starIndex}つ星",
                            tint = if (starIndex <= rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                if (rating > 0) {
                    Text(
                        text = "$rating / 5",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(null) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        },
        text = { content() }
    )
}