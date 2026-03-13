package com.example.oppurtunityscanner

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName ?: return
        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        // Ignore notifications that contain no text
        if (text.isBlank()) return

        // Capture notifications from apps like WhatsApp, Telegram, Gmail and LinkedIn
        val targetPackages = listOf(
            "com.whatsapp",
            "org.telegram.messenger",
            "com.google.android.gm",
            "com.linkedin.android"
        )

        if (packageName in targetPackages) {
            Log.d("NotificationListener", "Package: $packageName")
            Log.d("NotificationListener", "Title: $title")
            Log.d("NotificationListener", "Text: $text")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
