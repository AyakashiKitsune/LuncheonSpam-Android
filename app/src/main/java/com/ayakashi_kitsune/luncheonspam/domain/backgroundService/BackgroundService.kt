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
import android.widget.Toast
import androidx.room.Room
import com.ayakashi_kitsune.luncheonspam.data.SpamHamPhishRequest
import com.ayakashi_kitsune.luncheonspam.domain.contentSMSProvider.ContentSMSReceiver
import com.ayakashi_kitsune.luncheonspam.domain.database.AppDatabase
import com.ayakashi_kitsune.luncheonspam.domain.database.DAOSMSMessage
import com.ayakashi_kitsune.luncheonspam.domain.notificationService.NotificationService
import com.ayakashi_kitsune.luncheonspam.domain.serverService.ServerClientService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BackgroundService : Service() {
    /*for couroutine scope*/
    lateinit var cScope: CoroutineScope

    /*for intents what to receive and filter*/
    lateinit var intentFilter: IntentFilter

    /*service of receiving a broadcast sms received*/
    lateinit var broadcastReceiver: BroadcastReceiver

    /*notification service*/
    lateinit var notificationService: NotificationService

    /*to get sms from telephony db*/
    lateinit var contentSMSReceiver: ContentSMSReceiver

    /*predicter of sms*/
    lateinit var serverClientService: ServerClientService

    /*the database instance*/
    lateinit var database: AppDatabase

    /*the controller for table SMSmessages*/
    lateinit var smsDAOSMSMessage: DAOSMSMessage


    fun runningNotification() {
        cScope.launch {
            var totalSecs = 0L
            while (true) {
                val hours = totalSecs / 3600
                val minutes = (totalSecs % 3600) / 60
                val seconds = totalSecs % 60

                val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
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
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "LuncheonSpamDatabase"
        ).build()
        smsDAOSMSMessage = database.DAOSMSMessage()

        notificationService = NotificationService(applicationContext)
        contentSMSReceiver = ContentSMSReceiver(applicationContext)
        serverClientService = ServerClientService()

        cScope = CoroutineScope(SupervisorJob())

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                cScope.launch {
                    // get sms
                    val latestsms = contentSMSReceiver.getLatestSMS()
                    try {
                        // predict
                        val spamHamPhishRequest = SpamHamPhishRequest(listOf(latestsms.content))
                        val result = serverClientService.getpredictions(spamHamPhishRequest)
                        // service reacts to sms
                        result.forEach { sms ->
                            notificationService.createNotification(
                                1,
                                "Your SMS from ${latestsms.sender}",
                                // "sms received"
                                "Contains ${sms.links_found.size} links and considered as ${if (sms.is_spam) "spam" else "legit"} message"
                            )
                            smsDAOSMSMessage.addSMSMessages(
                                latestsms.copy(
                                    spamContent = sms.is_spam,
                                    linksFound = sms.links_found.map { it.link }
                                )
                            )
                        }
                    } catch (e: Exception) {
                        smsDAOSMSMessage.addSMSMessages(latestsms)
                        Toast.makeText(applicationContext, "err: server conn", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

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
        runningNotification()
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