package dev.korryr.shambaguard.ui.features.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.ui.features.admin.domain.model.AdminDashboardStats
import dev.korryr.shambaguard.ui.features.admin.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminHomeUiState(
    val isLoading: Boolean = false,
    val stats: AdminDashboardStats? = null,
    val error: String? = null,
)

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val repository: AdminRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminHomeUiState(isLoading = true))
    val uiState: StateFlow<AdminHomeUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getDashboardStats().collect { result ->
                result.fold(
                    onSuccess = { stats ->
                        _uiState.update { it.copy(isLoading = false, stats = stats) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    },
                )
            }
        }
    }
}
