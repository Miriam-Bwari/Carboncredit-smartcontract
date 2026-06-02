package dev.korryr.shambaguard.ui.features.farmer.presentation

// Status of each step in the minting pipeline
enum class PipelineStatus { DONE, ACTIVE, PENDING }

data class PipelineStep(
    val title:    String,
    val subtitle: String,
    val status:   PipelineStatus,
)

data class CarbonEarning(
    val title:      String,
    val date:       String,
    val amountKes:  Int,
    val completed:  Boolean,
)

data class CarbonUiState(
    // Header banner
    val carbonTonnes:   Float = 4.2f,
    val kesEquivalent:  Int   = 3_780,

    // Minting pipeline
    val pipelineSteps: List<PipelineStep> = listOf(
        PipelineStep("Data Collected",    "Farm scan complete",      PipelineStatus.DONE),
        PipelineStep("AI Verified",       "Biomass confirmed",       PipelineStatus.DONE),
        PipelineStep("Minted",            "4.2 tonnes registered",   PipelineStatus.ACTIVE),
        PipelineStep("Available to Sell", "Awaiting market match",   PipelineStatus.PENDING),
    ),

    // Real impact equivalents
    val treesEquivalent: Int = 18,
    val kmNotDriven:     Int = 420,

    // Earnings history
    val earnings: List<CarbonEarning> = listOf(
        CarbonEarning("M-Pesa Payout", "Oct 12, 2023", 2_150, true),
        CarbonEarning("M-Pesa Payout", "Jun 04, 2023", 1_800, true),
    ),

    val isLoading: Boolean = false,
)
