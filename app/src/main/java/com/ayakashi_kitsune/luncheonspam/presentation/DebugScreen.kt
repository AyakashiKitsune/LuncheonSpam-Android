package com.ayakashi_kitsune.luncheonspam.presentation

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ayakashi_kitsune.luncheonspam.LuncheonViewmodel
import com.ayakashi_kitsune.luncheonspam.domain.notificationListenerService.SniffIcationListenerService

@Composable
fun DebugScreen(
    viewmodel: LuncheonViewmodel
) {
    val context = LocalContext.current
    Column {
        Button(onClick = {
            context.startService(Intent(context, SniffIcationListenerService::class.java))
//            context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }) {
            Text(text = "get latest sms")
        }
    }
}