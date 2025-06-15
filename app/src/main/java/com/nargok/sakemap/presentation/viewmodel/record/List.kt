package com.nargok.sakemap.presentation.viewmodel.record

import androidx.lifecycle.ViewModel
import com.nargok.sakemap.domain.repository.DrinkRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrinkRecordListViewModel @Inject constructor(
    private val repository: DrinkRecordRepository
) : ViewModel() {

}
