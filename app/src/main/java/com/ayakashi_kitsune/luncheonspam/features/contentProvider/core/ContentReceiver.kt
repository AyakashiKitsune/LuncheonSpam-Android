package com.ayakashi_kitsune.luncheonspam.features.contentProvider.core

import android.Manifest
import android.content.Context
import android.net.Uri
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage


class ContentReceiver(
    private val context: Context
) {
    private val allMessages = Uri.parse("content://sms/inbox")
    fun displaySmsLog(): List<SMSMessage> {
        val projection = arrayOf(
            android.provider.Telephony.Sms._ID,
            android.provider.Telephony.Sms.ADDRESS,
            android.provider.Telephony.Sms.BODY,
            android.provider.Telephony.Sms.DATE,
//            android.provider.Telephony.Sms.THREAD_ID,
        )
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
}