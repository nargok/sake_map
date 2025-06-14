package com.nargok.sakemap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class RecordRegisterUiState(
    val drinkName: String = "",
    val selectedDrinkType: String = "",
    val selectedPrefecture: String = "",
    val rating: Int = 0,
    val selectedDate: LocalDate = LocalDate.now(),
    val description: String = "",
    val showDrinkTypeDropdown: Boolean = false,
    val showPrefectureDropdown: Boolean = false,
    val showDatePicker: Boolean = false,
    // Validation errors
    val drinkNameError: String? = null,
    val drinkTypeError: String? = null,
    val prefectureError: String? = null,
    val ratingError: String? = null
)

@HiltViewModel
class RecordRegisterViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(RecordRegisterUiState())
    val uiState: StateFlow<RecordRegisterUiState> = _uiState.asStateFlow()

    private val drinkTypes = listOf("日本酒", "ビール", "ウイスキー", "焼酎", "ワイン")
    private val prefectures = listOf(
        "北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県",
        "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県",
        "新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県",
        "静岡県", "愛知県", "三重県", "滋賀県", "京都府", "大阪府", "兵庫県",
        "奈良県", "和歌山県", "鳥取県", "島根県", "岡山県", "広島県", "山口県",
        "徳島県", "香川県", "愛媛県", "高知県", "福岡県", "佐賀県", "長崎県",
        "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"
    )

    fun getDrinkTypes(): List<String> = drinkTypes
    fun getPrefectures(): List<String> = prefectures

    fun updateDrinkName(name: String) {
        _uiState.value = _uiState.value.copy(
            drinkName = name,
            drinkNameError = validateDrinkName(name)
        )
    }

    fun updateSelectedDrinkType(type: String) {
        _uiState.value = _uiState.value.copy(
            selectedDrinkType = type,
            drinkTypeError = null,
            showDrinkTypeDropdown = false
        )
    }

    fun updateSelectedPrefecture(prefecture: String) {
        _uiState.value = _uiState.value.copy(
            selectedPrefecture = prefecture,
            prefectureError = null,
            showPrefectureDropdown = false
        )
    }

    fun updateRating(rating: Int) {
        _uiState.value = _uiState.value.copy(
            rating = rating,
            ratingError = null
        )
    }

    fun updateSelectedDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun updateDescription(description: String) {
        if (description.length <= 500) {
            _uiState.value = _uiState.value.copy(description = description)
        }
    }

    fun setShowDrinkTypeDropdown(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDrinkTypeDropdown = show)
    }

    fun setShowPrefectureDropdown(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPrefectureDropdown = show)
    }

    fun setShowDatePicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDatePicker = show)
    }

    fun saveRecord() {
        val currentState = _uiState.value
        val isValid = validateForm(
            currentState.drinkName,
            currentState.selectedDrinkType,
            currentState.selectedPrefecture,
            currentState.rating
        )

        if (isValid) {
            // TODO: Save logic - call repository to save the record
            // For now, just reset the form
            resetForm()
        }
    }

    private fun resetForm() {
        _uiState.value = RecordRegisterUiState()
    }

    private fun validateDrinkName(name: String): String? {
        return when {
            name.isBlank() -> "銘柄名を入力してください"
            name.length > 50 -> "銘柄名は50文字以内で入力してください"
            else -> null
        }
    }

    private fun validateForm(
        drinkName: String,
        drinkType: String,
        prefecture: String,
        rating: Int
    ): Boolean {
        val nameError = validateDrinkName(drinkName)
        val typeError = if (drinkType.isEmpty()) "お酒の種類を選択してください" else null
        val prefError = if (prefecture.isEmpty()) "都道府県を選択してください" else null
        val ratingError = if (rating == 0) "評価を選択してください" else null

        _uiState.value = _uiState.value.copy(
            drinkNameError = nameError,
            drinkTypeError = typeError,
            prefectureError = prefError,
            ratingError = ratingError
        )

        return nameError == null && typeError == null && prefError == null && ratingError == null
    }
}