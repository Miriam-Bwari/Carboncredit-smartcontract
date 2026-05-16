package dev.korryr.shambaguard.ui.features.farmer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// PolicyViewModel — owns tier selection and the M-Pesa STK Push stub flow.
// When the backend is ready (Week 2), replace simulateStkPush() with a real repository call.
@HiltViewModel
class PolicyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PolicyUiState())
    val uiState: StateFlow<PolicyUiState> = _uiState.asStateFlow()

    // Farmer taps a tier card — update selection immediately
    fun onTierSelected(tier: PolicyTier) {
        _uiState.update { it.copy(selectedTier = tier) }
    }

    // Farmer taps "Lipa na M-Pesa" — runs a 2-second stub to simulate the STK Push round-trip
    fun onPayWithMpesa() {
        val selected = _uiState.value.selectedTier ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(paymentState = PaymentState.Loading) }
            // TODO Week 2: replace with repository.initiateStkPush(selected.tierNumber, farmerId)
            delay(2_000L)
            _uiState.update { it.copy(paymentState = PaymentState.Success) }
        }
    }

    // Reset after a success dialog is dismissed or on error retry
    fun onResetPayment() {
        _uiState.update { it.copy(paymentState = PaymentState.Idle) }
    }
}
