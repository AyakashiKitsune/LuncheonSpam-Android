package com.ayakashi_kitsune.luncheonspam.utils

import java.util.Calendar

fun Long.getDateTimeFromMilis(): String {
    val calendar: Calendar = Calendar.getInstance()
    calendar.setTimeInMillis(this)

    val mYear: Int = calendar.get(Calendar.YEAR)
    val mMonth: Int = calendar.get(Calendar.MONTH)
    val mDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    return "$mMonth-$mDay-$mYear  $hour:$minute"
}