package dev.korryr.shambaguard.ui.features.farmer.presentation

// Insurance coverage tiers — KES amounts match the PRD (Section 3.3 & ShambaPool.sol)
enum class PolicyTier(
    val tierNumber:       Int,
    val nameKiswahili:    String,
    val nameEnglish:      String,
    val premiumKes:       Int,
    val coverageKes:      Int,
    val features:         List<String>,
    val isRecommended:    Boolean = false,
    val isPremiumDark:    Boolean = false,  // Nguvu card uses inverted dark green style
) {
    MSINGI(
        tierNumber     = 1,
        nameKiswahili  = "Msingi",
        nameEnglish    = "Basic",
        premiumKes     = 50,
        coverageKes    = 2_000,
        features       = listOf("Up to KES 2,000 payout", "Basic weather alerts"),
        isRecommended  = false,
        isPremiumDark  = false,
    ),
    IMARA(
        tierNumber     = 2,
        nameKiswahili  = "Imara",
        nameEnglish    = "Standard",
        premiumKes     = 150,
        coverageKes    = 8_000,
        features       = listOf(
            "Up to KES 8,000 payout",
            "Advanced weather alerts",
            "Priority SMS support",
        ),
        isRecommended  = true,
        isPremiumDark  = false,
    ),
    NGUVU(
        tierNumber     = 3,
        nameKiswahili  = "Nguvu",
        nameEnglish    = "Premium",
        premiumKes     = 400,
        coverageKes    = 25_000,
        features       = listOf(
            "Up to KES 25,000 payout",
            "Premium weather & soil data",
            "Dedicated agronomist call",
        ),
        isRecommended  = false,
        isPremiumDark  = true,
    ),
}

// All possible states for the M-Pesa stub payment flow
sealed class PaymentState {
    data object Idle       : PaymentState()
    data object Loading    : PaymentState()
    data object Success    : PaymentState()
    data class  Error(val message: String) : PaymentState()
}

data class PolicyUiState(
    val selectedTier:   PolicyTier?    = PolicyTier.IMARA,  // Pre-select recommended tier
    val paymentState:   PaymentState   = PaymentState.Idle,
)
