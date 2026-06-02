package dev.korryr.shambaguard.ui.features.auth.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Step 3 ViewModel — all selection and counter logic lives here
@HiltViewModel
class FarmPracticesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FarmPracticesUiState())
    val uiState: StateFlow<FarmPracticesUiState> = _uiState.asStateFlow()

    fun onCropToggled(crop: String) {
        _uiState.update { state ->
            val updated = if (crop in state.selectedCrops) {
                state.selectedCrops - crop
            } else {
                state.selectedCrops + crop
            }
            state.copy(selectedCrops = updated)
        }
    }

    fun onMethodSelected(method: String) {
        _uiState.update { it.copy(selectedMethod = method) }
    }

    fun onWaterSelected(water: String) {
        _uiState.update { it.copy(selectedWater = water) }
    }

    fun onIncrementTrees() {
        _uiState.update { it.copy(treeCount = it.treeCount + 1) }
    }

    fun onDecrementTrees() {
        _uiState.update { state ->
            state.copy(treeCount = maxOf(0, state.treeCount - 1))
        }
    }

    fun canComplete(): Boolean = _uiState.value.canComplete()
}
