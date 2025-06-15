package com.nargok.sakemap.presentation.viewmodel.record

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

data class PrefectureStatistics(
    val name: String,
    val count: Int
)

data class DrinkRecordMapUiState(
    val totalRecords: Int = 0,
    val completedPrefectures: Int = 0,
    val totalPrefectures: Int = 47,
    val mostPopularPrefecture: PrefectureStatistics? = null,
    val prefectureStatistics: List<PrefectureStatistics> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DrinkRecordMapViewModel @Inject constructor(
    private val repository: DrinkRecordRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DrinkRecordMapUiState())
    val uiState: StateFlow<DrinkRecordMapUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val records = repository.search()
                val statistics = calculateStatistics(records)

                _uiState.value = _uiState.value.copy(
                    totalRecords = records.size,
                    completedPrefectures = statistics.size,
                    mostPopularPrefecture = statistics.maxByOrNull { it.count },
                    prefectureStatistics = statistics,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "統計情報の読み込みに失敗しました: ${e.message}"
                )
            }
        }
    }

    private fun calculateStatistics(records: List<DrinkRecord>): List<PrefectureStatistics> {
        return records
            .groupBy { it.prefecture }
            .map { (prefecture, recordsList) ->
                PrefectureStatistics(
                    name = prefecture,
                    count = recordsList.size
                )
            }
            .sortedByDescending { it.count }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun refreshStatistics() {
        loadStatistics()
    }
}