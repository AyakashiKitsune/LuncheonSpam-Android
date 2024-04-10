package com.ayakashi_kitsune.luncheonspam.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MarkChatRead
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import com.ayakashi_kitsune.luncheonspam.ui.theme.LuncheonSpamTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

@Composable
fun AskPermissionsScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val permissionsFlow = flow {
        while (true) {

            val list = if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
                listOf(
                    NotificationManagerCompat.getEnabledListenerPackages(context)
                        .contains(context.packageName),
                    context.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED,
                    context.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED,
                    context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED,
                )
            } else {
                listOf(
                    NotificationManagerCompat.getEnabledListenerPackages(context)
                        .contains(context.packageName),
                    context.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED,
                    context.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED,
                )
            }
            emit(list)
            delay(1000)
        }
    }
    val permissions by permissionsFlow.collectAsState(initial = listOf(false, false, false))


    val askPostNotification =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}
    val askReadSMS =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}
    val askReceiveSMS =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    navHostController.navigate(Screenpaths.ProbablySpamMessageScreen.destination) {
                        this.popUpTo(Screenpaths.AskPermissionsScreen.destination) {
                            this.inclusive = true
                        }
                    }
                },
                enabled = permissions.reduce { acc, b -> acc and b },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "All setted up!")
            }
        }
    ) { padd ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padd)
                .padding(top = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
                CardPermissionsAndDetails(
                    title = "Make Notifications Permissions",
                    description = "Required permission to send warning notifications",
                    icon = Icons.Default.NotificationImportant,
                    permissionName = "Make Notifications Permissions",
                    onAskPermissions = {
                        askPostNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
                    },
                    isButtonDisabled = permissions.last()
                )
            }
            CardPermissionsAndDetails(
                title = "Listens Notifications Permissions",
                description = "Required permission to listen incoming Notifications in android",
                icon = Icons.Default.NotificationsActive,
                permissionName = "Notification Listener Permissions",
                onAskPermissions = {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                },
                isButtonDisabled = permissions[0]
            )
            CardPermissionsAndDetails(
                title = "Read SMS Permissions",
                description = "Required permission to read SMS inside android",
                icon = Icons.Default.MarkChatRead,
                permissionName = "Read SMS Permissions",
                onAskPermissions = {
                    askReadSMS.launch(Manifest.permission.READ_SMS)
                },
                isButtonDisabled = permissions[1]
            )
            CardPermissionsAndDetails(
                title = "Receive SMS Permissions",
                description = "Required permission to Receive SMS in android",
                icon = Icons.Default.Inbox,
                permissionName = "Receive SMS Permissions",
                onAskPermissions = {
                    askReceiveSMS.launch(Manifest.permission.RECEIVE_SMS)
                },
                isButtonDisabled = permissions[2]
            )
        }
    }
}


@Composable
fun CardPermissionsAndDetails(
    title: String,
    description: String,
    icon: ImageVector,
    permissionName: String,
    onAskPermissions: () -> Unit,
    isButtonDisabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints {
        val width = this.maxWidth
        Card(
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "grant $permissionName",
                    modifier = Modifier.padding(start = 8.dp)
                )
                Card(
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .requiredSizeIn(minWidth = width / 2)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp, 4.dp)
                    )
                }
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
            Button(
                onClick = onAskPermissions,
                enabled = !isButtonDisabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = permissionName)
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ShowCardPermissionsAndDetails() {
    LuncheonSpamTheme {
        Surface(
            color = Color.Gray,
            modifier = Modifier.fillMaxSize()
        ) {
            CardPermissionsAndDetails(
                title = "Lorem",
                description = "prodesset electram postulant cras quot iisque integer idque appetere labores liber cu tota tincidunt maximus urbanitas pellentesque pulvinar habitasse dicant",
                permissionName = "permission",
                icon = Icons.Default.Notifications,
                onAskPermissions = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}