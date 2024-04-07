package com.ayakashi_kitsune.luncheonspam.domain.contentSMSProvider

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage


class ContentSMSReceiver(
    private val context: Context
) {
    private val allMessages = Uri.parse("content://sms/inbox")
    private val projection = arrayOf(
        Telephony.Sms._ID,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.BODY,
        Telephony.Sms.DATE,
//            android.provider.Telephony.Sms.THREAD_ID,
    )

    fun getAllSMS(): List<SMSMessage> {
        val cursor = context.contentResolver.query(
            allMessages,
            projection,
            null,
            null,
            null
        )
        val rows = mutableListOf<SMSMessage>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val smsMessage = SMSMessage(
                    cursor.getInt(cursor.getColumnIndex(projection[0])),
                    cursor.getString(cursor.getColumnIndex(projection[1])),
                    cursor.getString(cursor.getColumnIndex(projection[2])),
                    cursor.getLong(cursor.getColumnIndex(projection[3])),
                )
//                for (i in 0 until cursor.columnCount) {
//                    Log.d(cursor.getColumnName(i) + "", cursor.getString(i) + "")
//                }
//                Log.d(
//                    "One row finished",
//                    "**************************************************"
//                )
                rows.add(smsMessage)
            }
            cursor.close()
        }
        return rows
    }

    fun getLatestSMS() {
        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            projection,
            null,
            null,
            "ORDER BY ${Telephony.Sms._ID} DESC LIMIT 1"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val smsMessage = SMSMessage(
                    cursor.getInt(cursor.getColumnIndex(projection[0])),
                    cursor.getString(cursor.getColumnIndex(projection[1])),
                    cursor.getString(cursor.getColumnIndex(projection[2])),
                    cursor.getLong(cursor.getColumnIndex(projection[3])),
                )
                println(smsMessage)
            }
        }
    }
}