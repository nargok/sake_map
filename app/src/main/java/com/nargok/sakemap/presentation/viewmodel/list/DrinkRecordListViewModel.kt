package com.nargok.sakemap.presentation.viewmodel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nargok.sakemap.domain.model.DrinkRecord
import com.nargok.sakemap.domain.repository.DrinkRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DrinkRecordListUiState(
    val drinkRecords: List<DrinkRecord> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDeleteDialog: Boolean = false,
    val recordToDelete: DrinkRecord? = null
)

@HiltViewModel
class DrinkRecordListViewModel @Inject constructor(
    private val repository: DrinkRecordRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DrinkRecordListUiState())
    val uiState: StateFlow<DrinkRecordListUiState> = _uiState.asStateFlow()

    init {
        loadDrinkRecords()
    }

    fun loadDrinkRecords() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val records = repository.search()
                _uiState.value = _uiState.value.copy(
                    drinkRecords = records,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "記録の読み込みに失敗しました: ${e.message}"
                )
            }
        }
    }

    fun showDeleteDialog(record: DrinkRecord) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            recordToDelete = record
        )
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            recordToDelete = null
        )
    }

    fun deleteRecord() {
        val recordToDelete = _uiState.value.recordToDelete ?: return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                repository.delete(recordToDelete.id.value)
                
                // Reload records after deletion
                loadDrinkRecords()
                
                hideDeleteDialog()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "削除に失敗しました: ${e.message}"
                )
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}