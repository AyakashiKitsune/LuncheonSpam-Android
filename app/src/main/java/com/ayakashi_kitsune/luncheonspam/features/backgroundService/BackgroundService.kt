package com.ayakashi_kitsune.luncheonspam.features.backgroundService

import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.ayakashi_kitsune.luncheonspam.features.NotificationService.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BackgroundService : Service() {
    lateinit var cScope: CoroutineScope
    lateinit var intentFilter: IntentFilter
    lateinit var broadcastReceiver: BroadcastReceiver
    fun runScope() {
        cScope.launch {
            for (i in 0..20) {
                Log.d("BackgroundService", "running")
                delay(1000)
            }
        }
    }

    override fun onCreate() {
        Log.d("BackgroundService", "created")

        cScope = CoroutineScope(SupervisorJob())
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                NotificationService(context!!).createNotification("from service", "service works")
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
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}