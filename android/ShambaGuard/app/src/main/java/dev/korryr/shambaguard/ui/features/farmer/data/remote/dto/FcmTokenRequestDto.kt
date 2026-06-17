package dev.korryr.shambaguard.ui.features.farmer.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequestDto(
    val farmer_id: String,
    val fcm_token: String
)
