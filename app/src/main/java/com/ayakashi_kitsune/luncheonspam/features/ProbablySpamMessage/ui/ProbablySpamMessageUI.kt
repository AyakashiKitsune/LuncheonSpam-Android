package com.ayakashi_kitsune.luncheonspam.features.ProbablySpamMessage.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ayakashi_kitsune.luncheonspam.backbone.LuncheonViewmodel
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage
import com.ayakashi_kitsune.luncheonspam.data.SMSMessageEvent
import com.ayakashi_kitsune.luncheonspam.features.contentProvider.core.ContentReceiver

@Composable
fun ProbablySpamMessageUI(
    viewmodel: LuncheonViewmodel,
    contentReceiver: ContentReceiver,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(key1 = true) {
        viewmodel.Setmessages(SMSMessageEvent.addAllSMS(contentReceiver.displaySmsLog()))
    }
    if (viewmodel.messagesList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Empty messages", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        val listOfSMS by remember {
            derivedStateOf {
                viewmodel.messagesList.groupBy { it.sender }
            }
        }
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(4.dp)
        ) {
            item {
                Row {
                    // recommended to set as spam
                    // setted as spams
                }
            }

            items(
                listOfSMS.keys.size,
                key = { listOfSMS.keys.toList()[it] }
            ) {
                MessageCard(
                    smsMessage = listOfSMS[listOfSMS.keys.toList()[it]]!!,
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun MessageCard(
    smsMessage: List<SMSMessage>,
    modifier: Modifier = Modifier
) {
    val maxSMS = 2
    val isLenghty by remember {
        derivedStateOf {
            smsMessage.size > maxSMS
        }
    }
    var isExpand by remember { mutableStateOf(false) }
    Card(
        modifier = modifier.animateContentSize()
    ) {
        Column(
            Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Sender : ${smsMessage[0].sender}",
                    style = MaterialTheme.typography.titleLarge
                )
                if (isLenghty) {
                    IconButton(onClick = { isExpand = !isExpand }) {
                        val icon = when (isExpand) {
                            true -> Icons.Default.KeyboardArrowUp
                            false -> Icons.Default.KeyboardArrowDown
                        }
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }
            }
            Text("Date : ${smsMessage[0].date}", style = MaterialTheme.typography.labelLarge)
            if (isLenghty and !isExpand) {
                smsMessage.take(maxSMS).map { sms ->
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            sms.content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            } else {
                smsMessage.map { sms ->
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            sms.content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}