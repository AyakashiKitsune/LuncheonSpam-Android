package com.ayakashi_kitsune.luncheonspam.domain.serverService

import com.ayakashi_kitsune.luncheonspam.data.SpamHamPhishRequest
import com.ayakashi_kitsune.luncheonspam.data.SpamHamPhishResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ServerClientService(
    host: String = "http://192.168.1.12",
    port: String = "5000"
) {
    private val link = "$host:$port/"
    private val CLIENT = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        engine {
            connectTimeout = 100_000
            socketTimeout = 100_000
        }
        install(Logging) {
            logger = Logger.DEFAULT
        }
    }

    suspend fun getpredictions(spamHamPhishRequest: SpamHamPhishRequest): List<SpamHamPhishResponse> {
        val result: List<SpamHamPhishResponse> = CLIENT.post(link) {
            this.setBody(spamHamPhishRequest)
            this.contentType(ContentType.Application.Json)

        }.body()
        return result
    }

}