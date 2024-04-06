package com.ayakashi_kitsune.luncheonspam.backbone

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage
import com.ayakashi_kitsune.luncheonspam.data.SMSMessageEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class LuncheonViewmodel : ViewModel() {
    private val messagesList = mutableStateListOf<SMSMessage>()
    val messageslist = flow {
        while (true) {
            val groupsms = messagesList.groupBy { it.sender }
            emit(groupsms)
            delay(1000)
        }
    }
    fun Setmessages(event: SMSMessageEvent) {
        when (event) {
            is SMSMessageEvent.addAllSMS -> {
                if (event.list != messagesList) {
                    messagesList.addAll(event.list)
                }
            }
            is SMSMessageEvent.addSMS -> messagesList.add(event.smsMessage)
        }
    }
}