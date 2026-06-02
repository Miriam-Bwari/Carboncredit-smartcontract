package dev.korryr.shambaguard.ui.features.farmer.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Week 5: replace stub with repository.getMyFarm(farmId) + repository.getPracticeLog(farmId)
@HiltViewModel
class MyFarmViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MyFarmUiState())
    val uiState: StateFlow<MyFarmUiState> = _uiState.asStateFlow()
}
