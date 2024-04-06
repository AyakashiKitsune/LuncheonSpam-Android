package com.ayakashi_kitsune.luncheonspam.backbone

sealed class Screenpaths(
    val destination: String,
) {
    object AskPermissionsScreen : Screenpaths("Permissions")
    object ProbablySpamMessageScreen : Screenpaths("Probably Spam Message")
    object SettingsScreen : Screenpaths("Settings")
}
