package com.ayakashi_kitsune.luncheonspam.data

data class SMSMessage(
    val id: Int,
    val sender: String,
    val content: String,
    val date: Long,
)

sealed class SMSMessageEvent {
    class addSMS(val smsMessage: SMSMessage) : SMSMessageEvent()
    class addAllSMS(val list: List<SMSMessage>) : SMSMessageEvent()
}