@file:OptIn(ExperimentalMaterial3Api::class)

package com.ayakashi_kitsune.luncheonspam.features.chatsview.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ayakashi_kitsune.luncheonspam.backbone.LuncheonViewmodel
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage
import com.ayakashi_kitsune.luncheonspam.utils.getDateTimeFromMilis

@Composable
fun ChatView(
    viewmodel: LuncheonViewmodel,
    index: String?,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val messages by viewmodel.messageslist.collectAsState(initial = emptyMap())
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = messages[index]?.get(0)?.sender ?: "contact number") })
        },
        modifier = modifier
    ) { padd ->
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier.padding(padd),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages[index]?.size ?: 0) {
                ChatSMS(smsMessage = messages[index]!![it])
            }
        }
    }
}

@Composable
fun ChatSMS(
    smsMessage: SMSMessage
) {
    var showDate by remember { mutableStateOf(false) }
    BoxWithConstraints(
        modifier = Modifier.clickable {
            showDate = !showDate
        }
    ) {
        val width = this.maxWidth
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            if (showDate) {
                Text(
                    text = smsMessage.date.getDateTimeFromMilis(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(8.dp))
                Surface {
                    if (Character.isLetter(smsMessage.sender[0])) {
                        Text(text = smsMessage.sender.slice(0..2))
                    } else {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = smsMessage.content, Modifier.padding(8.dp))
                }
            }
        }
    }
}