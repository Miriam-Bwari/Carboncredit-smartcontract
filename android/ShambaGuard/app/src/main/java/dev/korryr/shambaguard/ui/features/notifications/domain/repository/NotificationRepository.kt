package dev.korryr.shambaguard.ui.features.notifications.domain.repository

import dev.korryr.shambaguard.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    fun getUnreadCount(): Flow<Int>
    suspend fun insertNotification(notification: NotificationEntity)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun clearAll()
}
