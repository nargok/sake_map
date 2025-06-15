package com.nargok.sakemap.presentation.viewmodel.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nargok.sakemap.domain.model.DrinkRecord
import com.nargok.sakemap.domain.model.vo.DrinkType
import com.nargok.sakemap.domain.repository.DrinkRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
    // Loading and error states
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaveSuccessful: Boolean = false,
    // Validation errors
    val drinkNameError: String? = null,
    val drinkTypeError: String? = null,
    val prefectureError: String? = null,
    val ratingError: String? = null,
    val dateError: String? = null
)

@HiltViewModel
class RecordRegisterViewModel @Inject constructor(
    private val repository: DrinkRecordRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RecordRegisterUiState())
    val uiState: StateFlow<RecordRegisterUiState> = _uiState.asStateFlow()

    private val drinkTypes = listOf(
        "日本酒", "ビール", "ウイスキー", "焼酎", "ワイン",
        "ウォッカ", "ジン", "ラム", "リキュール"
    )
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
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            dateError = validateDate(date)
        )
    }

    fun updateSelectedDateFromMillis(millis: Long) {
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        updateSelectedDate(date)
    }

    fun getSelectedDateInMillis(): Long {
        return _uiState.value.selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
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

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(isSaveSuccessful = false)
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
            viewModelScope.launch {
                try {
                    // Set loading state
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )

                    // Convert string drink type to enum
                    val drinkType = convertStringToDrinkType(currentState.selectedDrinkType)
                    
                    // Create DrinkRecord domain object
                    val drinkRecord = DrinkRecord.create(
                        name = currentState.drinkName,
                        type = drinkType,
                        prefecture = currentState.selectedPrefecture,
                        rating = currentState.rating,
                        photoPath = null, // TODO: Add photo support later
                        description = if (currentState.description.isBlank()) null else currentState.description,
                    )

                    // Save using repository
                    repository.register(drinkRecord)

                    // Set success state and reset form
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSaveSuccessful = true,
                        errorMessage = null
                    )
                    
                    // Reset form after successful save
                    resetForm()
                    
                } catch (e: Exception) {
                    // Handle error
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "保存に失敗しました",
                        isSaveSuccessful = false
                    )
                    e.printStackTrace()
                }
            }
        }
    }

    private fun resetForm() {
        _uiState.value = RecordRegisterUiState()
    }

    private fun convertStringToDrinkType(drinkTypeString: String): DrinkType {
        return when (drinkTypeString) {
            "日本酒" -> DrinkType.SAKE
            "ビール" -> DrinkType.BEER
            "ウイスキー" -> DrinkType.WHISKEY
            "焼酎" -> DrinkType.SHOCHU
            "ワイン" -> DrinkType.WINE
            "ウォッカ" -> DrinkType.VODKA
            "ジン" -> DrinkType.GIN
            "ラム" -> DrinkType.RUM
            "リキュール" -> DrinkType.LIQUEUR
            else -> throw IllegalArgumentException("Unknown drink type: $drinkTypeString")
        }
    }

    private fun validateDrinkName(name: String): String? {
        return when {
            name.isBlank() -> "銘柄名を入力してください"
            name.length > 50 -> "銘柄名は50文字以内で入力してください"
            else -> null
        }
    }

    private fun validateDate(date: LocalDate): String? {
        return when {
            date.isAfter(LocalDate.now()) -> "未来の日付は選択できません"
            else -> null
        }
    }

    private fun validateForm(
        drinkName: String,
        drinkType: String,
        prefecture: String,
        rating: Int
    ): Boolean {
        val currentState = _uiState.value
        val nameError = validateDrinkName(drinkName)
        val typeError = if (drinkType.isEmpty()) "お酒の種類を選択してください" else null
        val prefError = if (prefecture.isEmpty()) "都道府県を選択してください" else null
        val ratingError = if (rating == 0) "評価を選択してください" else null
        val dateError = validateDate(currentState.selectedDate)

        _uiState.value = _uiState.value.copy(
            drinkNameError = nameError,
            drinkTypeError = typeError,
            prefectureError = prefError,
            ratingError = ratingError,
            dateError = dateError
        )

        return nameError == null && typeError == null && prefError == null && ratingError == null && dateError == null
    }
}