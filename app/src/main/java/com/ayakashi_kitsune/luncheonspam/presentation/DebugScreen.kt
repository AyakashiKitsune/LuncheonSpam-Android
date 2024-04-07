package com.ayakashi_kitsune.luncheonspam.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ayakashi_kitsune.luncheonspam.LuncheonViewmodel

@Composable
fun DebugScreen(
    viewmodel: LuncheonViewmodel
) {
    Column {
        Button(onClick = {

        }) {
            Text(text = "get latest sms")
        }
    }
}