package com.ayakashi_kitsune.luncheonspam.features.broadcastSMSReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.provider.Telephony
import android.widget.Toast

class BroadcastSMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
//            NotificationService(context!!).createNotification("spam service working","waaaawwwww")
            Toast.makeText(context, "sms received", Toast.LENGTH_SHORT).show()
        }
    }

    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        return super.peekService(myContext, service)
    }

    override fun getSentFromUid(): Int {
        return super.getSentFromUid()
    }

    override fun getSentFromPackage(): String? {
        return super.getSentFromPackage()
    }
}