package com.example.bettinalogistics.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.baseapp.di.Common
import com.example.bettinalogistics.R
import com.example.bettinalogistics.ui.activity.splash.SplashScreen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class LogisticsService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this@LogisticsService,SplashScreen::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this@LogisticsService, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            createNotificationChannel()
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_order_success)
                .setContentTitle(message.data["title"])
                .setContentText(message.data["content"])
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                val notificationId = 5
                notify(notificationId, builder.build())
            }
    }


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.lable_VNPAYMobilePos)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "12456"
    }
}