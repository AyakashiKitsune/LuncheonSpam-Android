package com.ayakashi_kitsune.luncheonspam.data

import kotlinx.serialization.Serializable

@Serializable
data class SpamHamPhishRequest(
    val message: List<String>
)