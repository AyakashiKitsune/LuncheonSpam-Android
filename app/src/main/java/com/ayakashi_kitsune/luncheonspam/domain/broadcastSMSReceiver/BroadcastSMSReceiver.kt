package com.ayakashi_kitsune.luncheonspam.domain.broadcastSMSReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.provider.Telephony
import android.widget.Toast
import com.ayakashi_kitsune.luncheonspam.domain.backgroundService.BackgroundService

class BroadcastSMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> {
                Toast.makeText(context, "sms received", Toast.LENGTH_SHORT).show()
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                context?.startService(Intent(context, BackgroundService::class.java))
            }
        }
    }

    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        return super.peekService(myContext, service)
    }

}