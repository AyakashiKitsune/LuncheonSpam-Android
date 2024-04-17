package com.ayakashi_kitsune.luncheonspam.domain.notificationListenerService

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.room.Room
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage
import com.ayakashi_kitsune.luncheonspam.data.SpamHamPhishRequest
import com.ayakashi_kitsune.luncheonspam.domain.backgroundService.BGServiceIntent
import com.ayakashi_kitsune.luncheonspam.domain.database.AppDatabase
import com.ayakashi_kitsune.luncheonspam.domain.database.DAOSMSMessage
import com.ayakashi_kitsune.luncheonspam.domain.notificationService.NotificationService
import com.ayakashi_kitsune.luncheonspam.domain.serverService.ServerClientService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar

class SniffIcationListenerService : NotificationListenerService() {

    lateinit var database: AppDatabase
    lateinit var smsDAOSMSMessage: DAOSMSMessage
    lateinit var serverClientService: ServerClientService
    lateinit var cScope: CoroutineScope
    lateinit var notificationService: NotificationService
    override fun onCreate() {
        Log.d("BGServiceSniff", "create")
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "LuncheonSpamDatabase"
        ).build()
        smsDAOSMSMessage = database.DAOSMSMessage()
        serverClientService = ServerClientService()
        cScope = CoroutineScope(SupervisorJob())
        notificationService = NotificationService(applicationContext)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            BGServiceIntent.START.name -> {

            }

            BGServiceIntent.STOP.name -> {
                onDestroy()
            }

            BGServiceIntent.CHANGE_HOST.name -> {
                val newHost = intent.getStringExtra("host")
                newHost?.let {
                    serverClientService = ServerClientService(host = newHost)
                }
            }

            BGServiceIntent.CHANGE_PORT.name -> {
                val newPort = intent.getStringExtra("port")
                newPort?.let {
                    serverClientService = ServerClientService(port = newPort)
                }
            }

            BGServiceIntent.CHANGE_HOST_AND_PORT.name -> {
                val newHost = intent.getStringExtra("host")
                val newPort = intent.getStringExtra("port")

                if (newPort != null && newHost != null) {
                    Log.d("BGServiceSniff", "notifhost port")
                    serverClientService = ServerClientService(newHost, newPort)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        Log.d("BGServiceSniff", "notif2")

        super.onNotificationPosted(sbn, rankingMap)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        Log.d("BGServiceSniff", "notif")
        val notif = sbn?.notification
        val packagename = sbn?.packageName
        val content = when (packagename) {
            PlatformFilter.GMAIL.packageName -> notif?.getEmail()
            PlatformFilter.MESSENGER.packageName -> notif?.getMessenger()
            else -> {
                null
            }
        }

        if (content != null) {
            cScope.launch {
                try {
                    // predict
                    val spamHamPhishRequest = SpamHamPhishRequest(listOf(content.content))
                    val result = serverClientService.getpredictions(spamHamPhishRequest)
                    // service reacts to sms
                    result.forEach { sms ->
                        notificationService.createNotification(
                            1,
                            "Your ${content.platform} from ${content.sender}",
                            // "sms received"
                            "Contains ${sms.links_found.size} links and considered as ${if (sms.is_spam) "spam" else "legit"} message ${if (sms.has_profanity) "but has profanity" else ""}"
                        )

                        smsDAOSMSMessage.addSMSMessages(
                            SMSMessage(
                                sender = content.sender,
                                content = content.content,
                                spamContent = sms.is_spam,
                                linksFound = sms.links_found.map { it.link },
                                platform = content.platform,
                                date = Calendar.getInstance().timeInMillis,
                                hasProfanity = sms.has_profanity
                            )
                        )
                    }
                } catch (e: Exception) {
                    smsDAOSMSMessage.addSMSMessages(
                        SMSMessage(
                            sender = content.sender,
                            content = content.content,
                            platform = content.platform,
                            date = Calendar.getInstance().timeInMillis,
                        )
                    )
                    Log.d("BGServicesniff", "err: server conn ${e.message}")
                }
            }

        }
        if (packagename != applicationContext.packageName) {
            val extras = notif?.extras

            val title = extras?.getCharSequence(Notification.EXTRA_TITLE)
            val text = extras?.getCharSequence(Notification.EXTRA_TEXT).toString()
            val subtxt = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT).toString()
            val bigtxt = extras?.getCharSequence(Notification.EXTRA_BIG_TEXT).toString()
            println("title: $title")
            println("text: $text")
            println("subtext: $subtxt")
            println("bigtext: $bigtxt")
        }
        super.onNotificationPosted(sbn)
    }

    override fun onDestroy() {
        Log.d("BGServiceSniff", "destroyed")
        database.close()
        super.onDestroy()
    }
}

open class OtherPlatformContent(
    val sender: String,
    val content: String,
    val platform: String
)

class EmailGmailContent(sender: String, content: String, platform: String = "Gmail") :
    OtherPlatformContent(
        sender, content,
        platform
    )

class MessengerContent(sender: String, content: String, platform: String = "Messenger") :
    OtherPlatformContent(
        sender, content,
        platform
    )

enum class PlatformFilter(val packageName: String) {
    MESSENGER("com.facebook.orca"),
    GMAIL("com.google.android.gm")
}

fun Notification.getEmail(): EmailGmailContent? {
    val bundle = this.extras
    val sender = bundle.getCharSequence(Notification.EXTRA_TITLE).toString()
    val content = bundle.getCharSequence(Notification.EXTRA_BIG_TEXT).toString()
    return if (sender == "null") {
        null
    } else {
        EmailGmailContent(sender, content)
    }
}

fun Notification.getMessenger(): MessengerContent? {
    val bundle = this.extras
    val sender = bundle.getCharSequence(Notification.EXTRA_TITLE).toString()
    val content = bundle.getCharSequence(Notification.EXTRA_TEXT).toString()
    return if (sender == "null") {
        null
    } else {
        MessengerContent(sender, content)
    }
}
