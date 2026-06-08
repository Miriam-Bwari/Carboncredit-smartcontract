package dev.korryr.shambaguard.ui.features.farmer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.ui.features.farmer.domain.repository.FarmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarbonViewModel @Inject constructor(
    private val farmRepository: FarmRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarbonUiState())
    val uiState: StateFlow<CarbonUiState> = _uiState.asStateFlow()

    init {
        loadCarbonData()
    }

    private fun loadCarbonData() {
        viewModelScope.launch {
            val farmerId = sessionManager.userIdFlow.firstOrNull() ?: return@launch

            val farmsResult = farmRepository.getFarmerFarms(farmerId).getOrNull()
            val firstFarm = farmsResult?.firstOrNull()

            if (firstFarm != null) {
                val farmId = firstFarm.id
                val carbonResult = farmRepository.getCarbonHistory(farmId).getOrNull()

                if (carbonResult != null) {
                    val rawTonnes = carbonResult.totalCarbonKg / 1000f
                    val tonnes = Math.round(rawTonnes * 10.0) / 10.0f

                    val kes = (carbonResult.totalCredits * 450).toInt() // Estimated market rate: 1 credit = 450 KES

                    val steps = mutableListOf<PipelineStep>()
                    steps.add(PipelineStep("Data Collected", "Farm scan complete", PipelineStatus.DONE))

                    if (tonnes > 0f) {
                        steps.add(PipelineStep("AI Verified", "Biomass confirmed", PipelineStatus.DONE))
                    } else {
                        steps.add(PipelineStep("AI Verification", "Awaiting sufficient biomass", PipelineStatus.PENDING))
                    }

                    if (carbonResult.totalCredits > 0f) {
                        val formattedCredits = "${Math.round(carbonResult.totalCredits * 10.0) / 10.0} tonnes registered"
                        steps.add(PipelineStep("Minted", formattedCredits, PipelineStatus.DONE))
                        steps.add(PipelineStep("Available to Sell", "Awaiting market match", PipelineStatus.ACTIVE))
                    } else {
                        steps.add(PipelineStep("Minting", "Pending AI verification", PipelineStatus.PENDING))
                        steps.add(PipelineStep("Available to Sell", "Awaiting credits", PipelineStatus.PENDING))
                    }

                    val earningsList = carbonResult.records.filter { (it.credits ?: 0f) > 0f }.map { record ->
                        // Parse "2026-06-08T05:26:40" to "2026-06-08"
                        val dateStr = try {
                            val idx = record.date.indexOf('T')
                            if (idx != -1) record.date.substring(0, idx) else record.date
                        } catch (e: Exception) {
                            record.date
                        }

                        val amount = ((record.credits ?: 0f) * 450).toInt()

                        CarbonEarning(
                            title = "Market Payout",
                            date = dateStr,
                            amountKes = amount,
                            completed = record.verified,
                        )
                    }

                    _uiState.update {
                        it.copy(
                            carbonTonnes = tonnes,
                            kesEquivalent = kes,
                            treesEquivalent = (tonnes * 4.5).toInt(),
                            kmNotDriven = (tonnes * 100).toInt(),
                            pipelineSteps = steps,
                            earnings = earningsList,
                        )
                    }
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
