package dev.korryr.shambaguard.ui.features.farmer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.FarmRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PolicyViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PolicyUiState())
    val uiState: StateFlow<PolicyUiState> = _uiState.asStateFlow()

    fun onTierSelected(tier: PolicyTier) {
        _uiState.update { it.copy(selectedTier = tier) }
    }

    fun onPayWithMpesa() {
        val selected = _uiState.value.selectedTier ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(paymentState = PaymentState.Loading) }

            val farmerId = sessionManager.userIdFlow.firstOrNull()
            if (farmerId == null) {
                _uiState.update { it.copy(paymentState = PaymentState.Error("Session expired")) }
                return@launch
            }

            // Get phone number
            val farmerResult = farmRepository.getFarmer(farmerId).getOrNull()
            if (farmerResult == null) {
                _uiState.update { it.copy(paymentState = PaymentState.Error("Failed to fetch user details")) }
                return@launch
            }

            val pushResult = farmRepository.triggerStkPush(
                farmerId = farmerId,
                phoneNumber = farmerResult.phoneNumber,
                amountKes = selected.premiumKes,
            )

            val pushResponse = pushResult.getOrNull()
            if (pushResponse != null && pushResponse.success && pushResponse.checkoutId != null) {
                pollPaymentStatus(pushResponse.checkoutId)
            } else {
                _uiState.update { it.copy(paymentState = PaymentState.Error(pushResponse?.message ?: "Payment initiation failed")) }
            }
        }
    }

    private suspend fun pollPaymentStatus(checkoutId: String) {
        // Poll every 3 seconds for up to 60 seconds (20 attempts)
        repeat(20) {
            delay(3000)
            val statusResult = farmRepository.getPaymentStatus(checkoutId).getOrNull()
            if (statusResult != null) {
                when (statusResult.status) {
                    "completed" -> {
                        _uiState.update { it.copy(paymentState = PaymentState.Success) }
                        return
                    }
                    "failed" -> {
                        _uiState.update { it.copy(paymentState = PaymentState.Error("Payment failed or was cancelled")) }
                        return
                    }
                    // If "pending", loop will continue
                }
            }
        }
        // Timeout
        _uiState.update { it.copy(paymentState = PaymentState.Error("Payment timeout. Check M-Pesa messages.")) }
    }

    fun onResetPayment() {
        _uiState.update { it.copy(paymentState = PaymentState.Idle) }
    }
}
