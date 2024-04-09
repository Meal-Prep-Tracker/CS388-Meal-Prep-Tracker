package com.example.mealpreptracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notifChannel"
const val channelName = "com.example.mealpreptracker"
class FirebaseNotifications : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.getNotification() != null) {
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }
    }

    fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.meal_prep_tracker_logo_notification)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(false)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(message)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())

    }
}