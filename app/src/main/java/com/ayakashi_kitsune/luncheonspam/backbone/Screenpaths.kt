package com.ayakashi_kitsune.luncheonspam.backbone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector

val navScreenList =
    listOf<Screenpaths>(Screenpaths.ProbablySpamMessageScreen, Screenpaths.SettingsScreen)
sealed class Screenpaths(
    val destination: String,
    val icon: ImageVector,
) {
    object AskPermissionsScreen : Screenpaths("Permissions", Icons.Default.VerifiedUser)
    object ProbablySpamMessageScreen :
        Screenpaths("Probably Spam Message", Icons.AutoMirrored.Filled.Message)

    object ChatViewScreen : Screenpaths("Chat View", Icons.AutoMirrored.Filled.Chat) {
        fun add(chatnumber: String): String {
            return "$destination/$chatnumber"
        }
    }

    object SettingsScreen : Screenpaths("Settings", Icons.Default.Settings)
}
