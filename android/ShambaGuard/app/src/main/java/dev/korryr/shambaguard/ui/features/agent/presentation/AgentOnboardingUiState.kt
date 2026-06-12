package dev.korryr.shambaguard.ui.features.agent.presentation

import com.google.android.gms.maps.model.LatLng

data class AgentOnboardingUiState(
    // Step 1: Details
    val name: String = "",
    val nationalId: String = "",
    val phone: String = "",

    // Step 2: Map
    val polygonPoints: List<LatLng> = emptyList(),
    val mapType: Int = com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID,
    val currentRegionName: String = "Detecting location...",

    // Step 3: Practices
    val cropType: String = "",
    val tillageMethod: String = "",
    val treeCount: Int = 0,
    val irrigationSource: String = "",

    // Step 4: Final State
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
)
