package com.ayakashi_kitsune.luncheonspam.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ayakashi_kitsune.luncheonspam.data.Converter
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage

@Database(entities = [SMSMessage::class], version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun DAOSMSMessage(): DAOSMSMessage

}