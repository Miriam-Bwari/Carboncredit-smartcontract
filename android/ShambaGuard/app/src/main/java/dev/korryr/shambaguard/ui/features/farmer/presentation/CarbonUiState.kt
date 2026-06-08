package dev.korryr.shambaguard.ui.features.farmer.presentation

// Status of each step in the minting pipeline
enum class PipelineStatus { DONE, ACTIVE, PENDING }

data class PipelineStep(
    val title: String,
    val subtitle: String,
    val status: PipelineStatus,
)

data class CarbonEarning(
    val title: String,
    val date: String,
    val amountKes: Int,
    val completed: Boolean,
)

data class CarbonUiState(
    // Header banner
    val carbonTonnes: Float = 0f,
    val kesEquivalent: Int = 0,

    // Minting pipeline
    val pipelineSteps: List<PipelineStep> = emptyList(),

    // Real impact equivalents
    val treesEquivalent: Int = 0,
    val kmNotDriven: Int = 0,

    // Earnings history
    val earnings: List<CarbonEarning> = emptyList(),

    val isLoading: Boolean = true,
)
