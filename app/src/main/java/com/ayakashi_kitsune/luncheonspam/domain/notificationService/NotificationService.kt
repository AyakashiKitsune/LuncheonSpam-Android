package com.ayakashi_kitsune.luncheonspam.domain.notificationService

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ayakashi_kitsune.luncheonspam.R

/**
 * Notification service
 *
 * @property context
 * @constructor Create empty Notification service
 */
class NotificationService(
    private val context: Context,
) {
    private val CHANNEL_ID = "Luncheon service"

    companion object {
        val SERVICE_STATUS_ID = 0
    }

    /**
     * Create channel
     *
     */
    fun createChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance).apply {
            description = CHANNEL_ID
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Create notification
     *
     * @param Notification_title
     * @param Notification_Content
     */
    @SuppressLint("MissingPermission")
    fun createNotification(
        id: Int,
        Notification_title: String,
        Notification_Content: String,
        showOnce: Boolean = false
    ) {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(Notification_title)
            .setContentText(Notification_Content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(showOnce)
            .build()

        NotificationManagerCompat.from(context).run {
            this.notify(id, builder)
        }
    }

    fun cancelNotification(id: Int) {
        NotificationManagerCompat.from(context).run {
            this.cancel(id)
        }
    }
}