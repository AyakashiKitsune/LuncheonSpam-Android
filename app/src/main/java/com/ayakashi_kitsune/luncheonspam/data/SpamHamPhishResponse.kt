package com.ayakashi_kitsune.luncheonspam.data

import kotlinx.serialization.Serializable

@Serializable
data class SpamHamPhishResponse(
    val has_profanity: Boolean,
    val is_spam: Boolean,
    val links_found: List<LinksFound>,
    val message: String
)

@Serializable
data class LinksFound(
    val isfraud_confidence: Double,
    val link: String
)