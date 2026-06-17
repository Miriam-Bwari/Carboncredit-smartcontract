package dev.korryr.shambaguard.ui.features.notifications.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dev.korryr.shambaguard.core.datastore.SessionManager
import dev.korryr.shambaguard.data.local.entity.NotificationEntity
import dev.korryr.shambaguard.data.local.entity.NotificationType
import dev.korryr.shambaguard.ui.features.farmer.data.remote.FarmApi
import dev.korryr.shambaguard.ui.features.farmer.data.remote.dto.FcmTokenRequestDto
import dev.korryr.shambaguard.ui.features.notifications.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ShambaFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var farmApi: FarmApi

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("Refreshed FCM token: $token")
        
        // When a new token is generated, we send it to the backend IF we have a logged-in farmer
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val farmerId = sessionManager.userIdFlow.firstOrNull()
                if (!farmerId.isNullOrBlank()) {
                    farmApi.updateFcmToken(FcmTokenRequestDto(farmer_id = farmerId, fcm_token = token))
                    Timber.d("Successfully synced new FCM token to backend for farmer: $farmerId")
                } else {
                    Timber.d("No farmer logged in, skipping token sync.")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to sync FCM token to backend")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("Received FCM message: ${remoteMessage.data}")

        // Assuming payload comes either in 'notification' or 'data' blocks.
        // We prioritize 'data' blocks if doing silent pushes, otherwise fallback to notification.
        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "Alert"
        val message = remoteMessage.data["message"] ?: remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: ""
        
        val typeString = remoteMessage.data["type"] ?: "SYSTEM"
        val type = try {
            NotificationType.valueOf(typeString.uppercase())
        } catch (e: Exception) {
            NotificationType.SYSTEM
        }

        val entity = NotificationEntity(
            title = title,
            message = message,
            type = type,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                notificationRepository.insertNotification(entity)
                Timber.d("Inserted incoming notification into local Room DB: $title")
            } catch (e: Exception) {
                Timber.e(e, "Failed to insert notification into DB")
            }
        }
    }
}
