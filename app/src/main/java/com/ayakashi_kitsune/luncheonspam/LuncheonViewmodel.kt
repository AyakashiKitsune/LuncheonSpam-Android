package com.ayakashi_kitsune.luncheonspam

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage
import com.ayakashi_kitsune.luncheonspam.data.SMSMessageEvent
import com.ayakashi_kitsune.luncheonspam.domain.contentSMSProvider.ContentSMSReceiver
import kotlinx.coroutines.flow.flow

class LuncheonViewmodelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LuncheonViewmodel(context) as T
    }
}


class LuncheonViewmodel(context: Context) : ViewModel() {

    private val contentReceiver: ContentSMSReceiver
    private val messagesList: SnapshotStateList<SMSMessage> = mutableStateListOf()
    val messageslist = flow {
        val groupsms = messagesList.groupBy { it.sender }
        emit(groupsms)
    }

    init {
        contentReceiver = ContentSMSReceiver(context)
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
}