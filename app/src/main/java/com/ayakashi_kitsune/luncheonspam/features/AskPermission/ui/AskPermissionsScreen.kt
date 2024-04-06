package com.ayakashi_kitsune.luncheonspam.features.AskPermission.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MarkChatRead
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ayakashi_kitsune.luncheonspam.backbone.Screenpaths
import com.ayakashi_kitsune.luncheonspam.ui.theme.LuncheonSpamTheme

@Composable
fun AskPermissionsScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current

    val permissions = remember {
        mutableListOf<Boolean>(
            context.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED,
            context.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val askReadSMS =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            permissions.set(0, it)
        }
    val askReceiveSMS =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            permissions.set(1, it)
        }
    val askPostNotification =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            permissions.set(2, it)
        }

    val icons = mutableListOf(Icons.Default.MarkChatRead, Icons.Default.Inbox)
    val manifestPermission =
        mutableListOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
    val actions = mutableListOf(askReadSMS, askReceiveSMS)
    val mapOfStringsCards = mutableListOf(
        listOf(
            "Read SMS Permissions",
            "Required permission to read SMS inside android",
            "Read SMS Permissions",
        ),
        listOf(
            "Receive SMS Permissions",
            "Required permission to Receive SMS in android",
            "Receive SMS Permissions",
        ),
    )
    if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
        mapOfStringsCards.add(
            listOf(
                "Make Notifications Permissions",
                "Required permission to send warning notifications",
                "Make Notifications Permissions",
            )
        )
        icons.add(Icons.Default.NotificationsActive)
        manifestPermission.add(Manifest.permission.POST_NOTIFICATIONS)
        actions.add(askPostNotification)
        permissions.add(context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }
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
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            mapOfStringsCards.mapIndexed { index, text ->
                CardPermissionsAndDetails(
                    title = text[0],
                    description = text[1],
                    icon = icons[index],
                    permissionName = text[2],
                    onAskPermissions = {
                        actions[index].launch(
                            manifestPermission[index]
                        )
                    },
                    isButtonDisabled = permissions[index],
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
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