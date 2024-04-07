package com.ayakashi_kitsune.luncheonspam.data

data class SMSMessage(
    val id: Int = 0,
    val sender: String = "",
    val content: String = "",
    val date: Long = 0,
)

sealed class SMSMessageEvent {
    class addSMS(val smsMessage: SMSMessage) : SMSMessageEvent()
    class addAllSMS(val list: List<SMSMessage>) : SMSMessageEvent()
}