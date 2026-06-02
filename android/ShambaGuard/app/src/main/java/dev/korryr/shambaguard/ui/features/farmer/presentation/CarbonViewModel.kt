package dev.korryr.shambaguard.ui.features.farmer.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Week 5: replace stub with repository.getCarbonCredits(farmId)
@HiltViewModel
class CarbonViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CarbonUiState())
    val uiState: StateFlow<CarbonUiState> = _uiState.asStateFlow()
}
