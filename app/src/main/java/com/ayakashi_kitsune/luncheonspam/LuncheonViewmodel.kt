package com.ayakashi_kitsune.luncheonspam

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage
import com.ayakashi_kitsune.luncheonspam.data.SMSMessageEvent
import com.ayakashi_kitsune.luncheonspam.domain.broadcastSMSReceiver.BroadcastSMSReceiver
import com.ayakashi_kitsune.luncheonspam.domain.contentSMSProvider.ContentSMSReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class LuncheonViewmodelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LuncheonViewmodel(context) as T
    }
}


class LuncheonViewmodel(private val context: Context) : ViewModel() {

    private val contentReceiver: ContentSMSReceiver
    private val messagesList: SnapshotStateList<SMSMessage> = mutableStateListOf()
    private val broadcastSMSReceiver: BroadcastSMSReceiver
    val messageslist = flow {
        while (true) {
            val groupsms = messagesList.groupBy { it.sender }
            emit(groupsms)
            println("reading")
            delay(3000)
        }
    }

    init {
        contentReceiver = ContentSMSReceiver(context)
        broadcastSMSReceiver = object : BroadcastSMSReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                    val sms = contentReceiver.getLatestSMS()
                    setMessages(SMSMessageEvent.addSMS(sms))
                    Toast.makeText(context, sms.toString(), Toast.LENGTH_SHORT).show()
                    println("ran viewmodel broadcaster")
                }
                super.onReceive(context, intent)
            }
        }
        context.registerReceiver(broadcastSMSReceiver, IntentFilter().apply {
            addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        })
        setMessages(SMSMessageEvent.addAllSMS(contentReceiver.getAllSMS()))
    }

    fun setMessages(event: SMSMessageEvent) {
        when (event) {
            is SMSMessageEvent.addAllSMS -> {
                if (event.list != messagesList) {
                    messagesList.addAll(event.list)
                }
            }
            is SMSMessageEvent.addSMS -> messagesList.add(event.smsMessage)
        }
    }

    override fun onCleared() {
        context.unregisterReceiver(broadcastSMSReceiver)
        super.onCleared()
    }
}