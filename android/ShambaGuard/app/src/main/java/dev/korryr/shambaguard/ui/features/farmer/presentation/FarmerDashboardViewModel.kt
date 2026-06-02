package dev.korryr.shambaguard.ui.features.farmer.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// FarmerDashboardViewModel — holds dashboard UI state.
// Week 5 integration: replace stub data with repository calls to
// GET /api/v1/farms/{farm_id}/carbon-report and GET /api/v1/farms/{farm_id}
@HiltViewModel
class FarmerDashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FarmerDashboardUiState())
    val uiState: StateFlow<FarmerDashboardUiState> = _uiState.asStateFlow()
}
