package com.ayakashi_kitsune.luncheonspam.backbone

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage
import com.ayakashi_kitsune.luncheonspam.data.SMSMessageEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LuncheonViewmodel : ViewModel() {
    val messagesList = mutableStateListOf<SMSMessage>()
    fun Setmessages(event: SMSMessageEvent) {
        when (event) {
            is SMSMessageEvent.addAllSMS -> messagesList.addAll(event.list)
            is SMSMessageEvent.addSMS -> messagesList.add(event.smsMessage)
        }
    }
}