package com.ayakashi_kitsune.luncheonspam.domain.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage

private val tablename = "SpamHamTable"

@Dao
interface DAOSMSMessage {

    @Query("SELECT * FROM SpamHamTable")
    fun getSMSMessages(): List<SMSMessage>

    @Insert()
    fun addSMSMessages(smsMessage: SMSMessage)

    @Insert(SMSMessage::class, onConflict = OnConflictStrategy.IGNORE)
    fun addAllSMSMessages(listSms: List<SMSMessage>)

    @Update()
    fun setSMSMessages(vararg smsMessage: SMSMessage)
}