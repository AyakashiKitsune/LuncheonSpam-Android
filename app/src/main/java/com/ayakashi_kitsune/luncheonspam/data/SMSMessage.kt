package com.ayakashi_kitsune.luncheonspam.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity("SpamHamTable")
data class SMSMessage(
    @PrimaryKey(true)
    val id: Int = 0,

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val sender: String = "",

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val content: String = "",

    @ColumnInfo()
    val date: Long = 0,

    @ColumnInfo()
    val spamContent: Boolean = false,

    @ColumnInfo()
    val linksFound: List<String> = emptyList()
)

class Converter {

    @TypeConverter
    fun fromList(value: List<String>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)
}