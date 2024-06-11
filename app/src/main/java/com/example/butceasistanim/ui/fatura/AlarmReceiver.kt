package com.example.butceasistanim.ui.fatura

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", 0)
        val notification: Notification? = intent.getParcelableExtra("notification")

        if (notification == null) {
            Log.e("AlarmReceiver", "Notification is null")
            return
        }

        if (ActivityCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } else {
            Log.e("AlarmReceiver", "Notification permission not granted")
        }
    }
}
