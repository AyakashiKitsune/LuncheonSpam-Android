package com.ayakashi_kitsune.luncheonspam.domain.backgroundService

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.ayakashi_kitsune.luncheonspam.domain.notificationService.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BackgroundService : Service() {
    lateinit var cScope: CoroutineScope
    lateinit var intentFilter: IntentFilter
    lateinit var broadcastReceiver: BroadcastReceiver
    lateinit var notificationService: NotificationService
    var totalSecs: Long = 0L
    fun runScope() {
        cScope.launch {
            while (true) {
                val hours = totalSecs / 3600;
                val minutes = (totalSecs % 3600) / 60;
                val seconds = totalSecs % 60;

                val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                notificationService.createNotification(
                    NotificationService.SERVICE_STATUS_ID,
                    "Luncheon Spam is Running",
                    timeString,
                    showOnce = true
                )
                totalSecs++
                delay(1000)
            }
        }
    }

    override fun onCreate() {
        Log.d("BackgroundService", "created")
        notificationService = NotificationService(applicationContext)
        cScope = CoroutineScope(SupervisorJob())
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                NotificationService(context!!).createNotification(
                    1,
                    "from service",
                    "service works"
                )
            }
        }
        intentFilter = IntentFilter().apply {
            this.addAction("android.provider.Telephony.SMS_RECEIVED")
        }
        if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
            registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, intentFilter)
        }
        runScope()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BackgroundService", "started command")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun startService(service: Intent?): ComponentName? {
        Log.d("BackgroundService", "started")
        return super.startService(service)
    }

    override fun onDestroy() {
        Log.d("BackgroundService", "destroyed")
        unregisterReceiver(broadcastReceiver)
        cScope.cancel()
        notificationService.cancelNotification(NotificationService.SERVICE_STATUS_ID)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}