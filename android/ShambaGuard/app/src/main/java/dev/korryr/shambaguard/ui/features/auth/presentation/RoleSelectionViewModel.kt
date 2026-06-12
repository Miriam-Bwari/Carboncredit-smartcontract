package dev.korryr.shambaguard.ui.features.auth.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ---------------------------------------------------------------------------
// RoleSelectionViewModel.kt
// Owns the state for the "Choose Your Role" screen.
// No Android Context. No business logic in the UI layer.
// ---------------------------------------------------------------------------
@HiltViewModel
class RoleSelectionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RoleSelectionUiState())
    val uiState: StateFlow<RoleSelectionUiState> = _uiState.asStateFlow()

    fun onRoleSelected(role: AppUserRole) {
        _uiState.update { it.copy(selectedRole = role) }
    }
}
