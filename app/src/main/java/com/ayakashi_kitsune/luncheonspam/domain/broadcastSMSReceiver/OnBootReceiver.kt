package com.ayakashi_kitsune.luncheonspam.domain.broadcastSMSReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.ayakashi_kitsune.luncheonspam.domain.backgroundService.BackgroundService

open class OnBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Toast.makeText(context, "running app", Toast.LENGTH_SHORT).show()
                context?.startService(Intent(context, BackgroundService::class.java))
            }
        }
    }

    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        return super.peekService(myContext, service)
    }

}