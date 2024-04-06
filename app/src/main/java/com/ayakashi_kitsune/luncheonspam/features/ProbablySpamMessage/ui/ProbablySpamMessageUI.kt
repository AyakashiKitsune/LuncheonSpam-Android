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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ayakashi_kitsune.luncheonspam.backbone.LuncheonViewmodel
import com.ayakashi_kitsune.luncheonspam.backbone.Screenpaths
import com.ayakashi_kitsune.luncheonspam.data.SMSMessage
import com.ayakashi_kitsune.luncheonspam.data.SMSMessageEvent
import com.ayakashi_kitsune.luncheonspam.features.contentProvider.core.ContentReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProbablySpamMessageUI(
    viewmodel: LuncheonViewmodel,
    navHostController: NavHostController,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current
    val contentReceiver = remember {
        ContentReceiver(context)
    }
    val listOfSMS: Map<String, List<SMSMessage>> by viewmodel.messageslist.collectAsState(initial = emptyMap())
    LaunchedEffect(key1 = true) {
        withContext(Dispatchers.IO) {
            viewmodel.Setmessages(SMSMessageEvent.addAllSMS(contentReceiver.displaySmsLog()))
        }
    }
    if (listOfSMS.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Empty messages", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
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
                    showChat = {
                        navHostController.navigate(Screenpaths.ChatViewScreen.add(listOfSMS.keys.toList()[it]))
                    },
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
    showChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.animateContentSize(),
        onClick = showChat
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
                    "${smsMessage[0].sender}",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}