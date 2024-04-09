package com.ayakashi_kitsune.luncheonspam

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ayakashi_kitsune.luncheonspam.domain.contentSMSProvider.ContentSMSReceiver
import com.ayakashi_kitsune.luncheonspam.domain.database.AppDatabase
import com.ayakashi_kitsune.luncheonspam.domain.database.DAOSMSMessage
import com.ayakashi_kitsune.luncheonspam.domain.notificationService.NotificationService
import com.ayakashi_kitsune.luncheonspam.domain.serverService.ServerClientService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LuncheonViewmodelFactory(
    private val context: Context,
    private val database: AppDatabase,
    private val host: String,
    private val port: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LuncheonViewmodel(context, database, host, port) as T
    }
}


class LuncheonViewmodel(
    context: Context,
    database: AppDatabase,
    host: String,
    port: String,
) : ViewModel() {
    private val contentReceiver: ContentSMSReceiver

    //    private val broadcastSMSReceiver: BroadcastReceiver
    private val serverClientService: ServerClientService
    private val notificationService: NotificationService

    private val smsDAOSMSMessage: DAOSMSMessage = database.DAOSMSMessage()

    val messageslist = flow {
        while (true) {
            emit(smsDAOSMSMessage.getSMSMessages())
            delay(3000)
        }

    }
        .catch {
            Log.d("messageListErr", it.message.toString())
        }
        .onEach {
            println("running sms flow")
        }
        .map { listsms ->
            listsms.groupBy {
                it.sender
            }
        }.flowOn(Dispatchers.IO)

    init {
        contentReceiver = ContentSMSReceiver(context)
        notificationService = NotificationService(context)
        serverClientService = ServerClientService()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                smsDAOSMSMessage.addAllSMSMessages(contentReceiver.getAllSMS())
            }
        }
    }
}