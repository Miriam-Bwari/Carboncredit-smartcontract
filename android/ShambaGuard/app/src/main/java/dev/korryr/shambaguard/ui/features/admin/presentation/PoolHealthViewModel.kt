package dev.korryr.shambaguard.ui.features.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.ui.features.admin.domain.model.PoolHealth
import dev.korryr.shambaguard.ui.features.admin.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PoolHealthUiState(
    val isLoading: Boolean = false,
    val poolHealth: PoolHealth? = null,
    val error: String? = null,
)

@HiltViewModel
class PoolHealthViewModel @Inject constructor(
    private val repository: AdminRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PoolHealthUiState(isLoading = true))
    val uiState: StateFlow<PoolHealthUiState> = _uiState.asStateFlow()

    init {
        loadPoolHealth()
    }

    fun loadPoolHealth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getPoolHealth().collect { result ->
                result.fold(
                    onSuccess = { health ->
                        _uiState.update { it.copy(isLoading = false, poolHealth = health) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    },
                )
            }
        }
    }
}
