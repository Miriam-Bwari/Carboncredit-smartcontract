package dev.korryr.shambaguard.ui.features.farmer.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Shared ViewModel for both drought screens — holds early warning + insights state.
// Week 5: replace stubs with repository.getEarlyWarning(farmId) and repository.getFarmReport(farmId)
@HiltViewModel
class DroughtViewModel @Inject constructor() : ViewModel() {

    private val _warningState = MutableStateFlow(EarlyWarningUiState())
    val warningState: StateFlow<EarlyWarningUiState> = _warningState.asStateFlow()

    private val _insightsState = MutableStateFlow(DroughtInsightsUiState())
    val insightsState: StateFlow<DroughtInsightsUiState> = _insightsState.asStateFlow()
}
