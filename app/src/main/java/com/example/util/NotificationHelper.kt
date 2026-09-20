package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class InAppAlert(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val isMatchAlert: Boolean = true,
    val tournamentId: String? = null
)

object NotificationHelper {

    const val CHANNEL_MATCH_ALERTS = "channel_ff_match_alerts"
    const val CHANNEL_ADMIN_UPDATES = "channel_ff_admin_updates"

    private const val NOTIFICATION_MATCH_BASE_ID = 1000
    private const val NOTIFICATION_ADMIN_BASE_ID = 2000

    private val _inAppAlerts = MutableSharedFlow<InAppAlert>(extraBufferCapacity = 10)
    val inAppAlerts: SharedFlow<InAppAlert> = _inAppAlerts.asSharedFlow()

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Match Starting Alert Channel
            val matchChannel = NotificationChannel(
                CHANNEL_MATCH_ALERTS,
                "Match Starting Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts when registered tournament matches are about to start or room IDs are released"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }

            // Admin Announcements & Updates Channel
            val adminChannel = NotificationChannel(
                CHANNEL_ADMIN_UPDATES,
                "Admin & Tournament Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Official announcements, rule changes, and bracket updates from tournament admins"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 150, 100, 150)
            }

            notificationManager.createNotificationChannel(matchChannel)
            notificationManager.createNotificationChannel(adminChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Dispatches a notification when a registered tournament match is starting.
     */
    fun sendMatchStartingAlert(
        context: Context,
        tournamentId: String,
        tournamentTitle: String,
        matchTime: String = "Starting Now",
        roomId: String? = null,
        password: String? = null
    ) {
        createNotificationChannels(context)

        // Also push in-app alert for active screen engagement
        _inAppAlerts.tryEmit(
            InAppAlert(
                title = "⚔️ MATCH STARTING: $tournamentTitle",
                message = if (!roomId.isNullOrBlank()) "Room ID: $roomId | Pass: $password. Join Custom Room now!" else "Match starts at $matchTime. Get your squad ready!",
                isMatchAlert = true,
                tournamentId = tournamentId
            )
        )

        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAVIGATE_TO", "match_room")
            putExtra("EXTRA_TOURNAMENT_ID", tournamentId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            tournamentId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val roomInfoText = if (!roomId.isNullOrBlank()) {
            "Room ID: $roomId | Pass: ${password ?: "None"} • Join within 5 mins!"
        } else {
            "Your registered match starts at $matchTime. Assemble your squad in Custom Room!"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_MATCH_ALERTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("🔥 Tournament Match Starting!")
            .setContentText("$tournamentTitle — $roomInfoText")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("🔥 $tournamentTitle is starting!\n$roomInfoText\nDo not be late or slot will be forfeited.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationId = NOTIFICATION_MATCH_BASE_ID + (tournamentId.hashCode() % 500)
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Handled safely
        }
    }

    /**
     * Dispatches a high-priority alert to all joined contenders when Room ID and Password are published or updated.
     */
    fun sendRoomIdUpdatedAlert(
        context: Context,
        tournamentId: String,
        tournamentTitle: String,
        roomId: String,
        password: String,
        joinedPlayerCount: Int = 0
    ) {
        createNotificationChannels(context)

        val roomAlertMsg = "🔑 Room ID: $roomId | Pass: $password. Open Free Fire, navigate to Custom Room, and enter your slot now!"

        // Emit instant in-app alert banner
        _inAppAlerts.tryEmit(
            InAppAlert(
                title = "🚨 ROOM CREDENTIALS RELEASED: $tournamentTitle",
                message = roomAlertMsg,
                isMatchAlert = true,
                tournamentId = tournamentId
            )
        )

        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAVIGATE_TO", "match_room")
            putExtra("EXTRA_TOURNAMENT_ID", tournamentId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            ("room_update_$tournamentId").hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val recipientLabel = if (joinedPlayerCount > 0) "Alert sent to $joinedPlayerCount joined contenders" else "All joined players"

        val builder = NotificationCompat.Builder(context, CHANNEL_MATCH_ALERTS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("🔑 Room ID & Pass: $tournamentTitle")
            .setContentText("Room ID: $roomId | Pass: $password. Join Custom Room!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("🔥 Free Fire Custom Room Details\n\nTournament: $tournamentTitle\nRoom ID: $roomId\nPassword: $password\n\n$recipientLabel. Launch Free Fire and join Custom Room immediately!")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationId = NOTIFICATION_MATCH_BASE_ID + 500 + (tournamentId.hashCode() % 500)
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Handled safely
        }
    }

    /**
     * Dispatches a notification when an admin posts an announcement or updates tournament status.
     */
    fun sendAdminUpdateAlert(
        context: Context,
        author: String,
        message: String,
        tournamentTitle: String? = null
    ) {
        createNotificationChannels(context)

        _inAppAlerts.tryEmit(
            InAppAlert(
                title = "📢 Admin Update by $author",
                message = message,
                isMatchAlert = false
            )
        )

        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAVIGATE_TO", "announcements")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (tournamentTitle != null) "📢 Admin Update: $tournamentTitle" else "📢 Tournament Admin Announcement"

        val builder = NotificationCompat.Builder(context, CHANNEL_ADMIN_UPDATES)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText("[$author]: $message")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Official update from $author:\n$message")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationId = NOTIFICATION_ADMIN_BASE_ID + (System.currentTimeMillis() % 500).toInt()
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Handled safely
        }
    }

    /**
     * Dispatches general notifications (e.g. Wallet Deposits, UPI Cashouts).
     */
    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        isMatchAlert: Boolean = false,
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt()
    ) {
        createNotificationChannels(context)

        _inAppAlerts.tryEmit(
            InAppAlert(
                title = title,
                message = message,
                isMatchAlert = isMatchAlert
            )
        )

        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAVIGATE_TO", "profile")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (isMatchAlert) CHANNEL_MATCH_ALERTS else CHANNEL_ADMIN_UPDATES
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Handled safely
        }
    }
}
