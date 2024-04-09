package com.ayakashi_kitsune.luncheonspam.domain.modelInferencing

import android.util.Log
import com.ayakashi_kitsune.luncheonspam.data.SpamHamPhishRequest
import com.ayakashi_kitsune.luncheonspam.domain.serverService.ServerClientService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ModelInferencing(
    private val serverClientService: ServerClientService,
    private val coroutineScope: CoroutineScope,
    private val SERVICETAG: String
) {
    fun predict(messages: List<String>) {
        coroutineScope.launch {
            try {
                val spamHamPhishRequest = SpamHamPhishRequest(messages)
                val result = serverClientService.getpredictions(spamHamPhishRequest)
                Log.d(SERVICETAG, "prediction: $result")
            } catch (e: Exception) {
                Log.d(SERVICETAG, "error : ${e.message}")
            }
        }
    }
}