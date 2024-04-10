package com.ayakashi_kitsune.luncheonspam

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.ayakashi_kitsune.luncheonspam.domain.backgroundService.BGServiceIntent
import com.ayakashi_kitsune.luncheonspam.domain.backgroundService.BackgroundService
import com.ayakashi_kitsune.luncheonspam.domain.database.AppDatabase
import com.ayakashi_kitsune.luncheonspam.domain.notificationService.NotificationService
import com.ayakashi_kitsune.luncheonspam.presentation.AskPermissionsScreen
import com.ayakashi_kitsune.luncheonspam.presentation.ChatView
import com.ayakashi_kitsune.luncheonspam.presentation.DebugScreen
import com.ayakashi_kitsune.luncheonspam.presentation.ProbablySpamMessageScreen
import com.ayakashi_kitsune.luncheonspam.presentation.Screenpaths
import com.ayakashi_kitsune.luncheonspam.presentation.SettingsScreen
import com.ayakashi_kitsune.luncheonspam.presentation.navScreenList
import com.ayakashi_kitsune.luncheonspam.ui.theme.LuncheonSpamTheme

class LuncheonSpamApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val notificationService = NotificationService(this)
        notificationService.createChannel()

    }
}

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    val host = "http://192.168.1.12"
    val port = "5000"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(
            context = this.applicationContext,
            klass = AppDatabase::class.java,
            name = "LuncheonSpamDatabase"
        ).build()
        val viewmodel: LuncheonViewmodel by viewModels {
            LuncheonViewmodelFactory(
                context = this.applicationContext,
                database = database,
            )
        }

        setContent {
            LuncheonSpamTheme {
                val navHostController = rememberNavController()
                var selectedNavIndex by remember {
                    mutableStateOf<Screenpaths>(Screenpaths.AskPermissionsScreen)
                }
                Scaffold(
                    bottomBar = {
                        LuncheonNavigationBar(
                            navHostController = navHostController,
                            selectedNavIndex = selectedNavIndex,
                            setSelectedNavIndex = {
                                selectedNavIndex = it
                            }
                        )
                    }
                ) { padd ->
                    /*surface for all text to contrast automatically*/
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padd),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        NavHost(
                            navController = navHostController,
                            startDestination = Screenpaths.AskPermissionsScreen.destination
                        ) {
                            composable(Screenpaths.AskPermissionsScreen.destination) {
                                val permissions = remember {
                                    /*
                                    * when the device is android 13 and above for post notifications
                                    * */
                                    if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
                                        listOf<Boolean>(
                                            applicationContext.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED,
                                            applicationContext.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED,
                                            applicationContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED,
                                            NotificationManagerCompat.getEnabledListenerPackages(
                                                applicationContext
                                            ).contains(applicationContext.packageName),

                                        )
                                    } else {
                                        /*
                                        * when the device is lower than android 13
                                        * */
                                        listOf<Boolean>(
                                            applicationContext.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED,
                                            applicationContext.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED,
                                            NotificationManagerCompat.getEnabledListenerPackages(
                                                applicationContext
                                            ).contains(applicationContext.packageName)
                                        )
                                    }
                                }
                                /*
                                * if every permissions has true then navigate to main screen
                                * */
                                if (permissions.reduce { acc, b -> acc and b }) {
                                    navHostController.navigate(Screenpaths.ProbablySpamMessageScreen.destination) {
                                        this.popUpTo(Screenpaths.AskPermissionsScreen.destination) {
                                            inclusive = true
                                        }
                                    }
                                    selectedNavIndex = Screenpaths.ProbablySpamMessageScreen
                                }
                                /* else just show the ask perissions screen*/
                                AskPermissionsScreen(navHostController = navHostController)
                            }

                            composable(
                                Screenpaths.ProbablySpamMessageScreen.destination,
                                enterTransition = { slideInHorizontally() }
                            ) {
                                startService(
                                    Intent(
                                        applicationContext,
                                        BackgroundService::class.java
                                    ).apply {
                                        action = BGServiceIntent.CHANGE_HOST_AND_PORT.name
                                        putExtra("host", host)
                                        putExtra("port", port)
                                    }
                                )
                                selectedNavIndex = Screenpaths.ProbablySpamMessageScreen
                                ProbablySpamMessageScreen(
                                    viewmodel = viewmodel,
                                    navHostController,
                                    onClickChat = {
                                        navHostController.navigate(Screenpaths.ChatViewScreen.add(it))
                                    }
                                )
                            }

                            composable(
                                Screenpaths.ChatViewScreen.destination + "/{index}",
                                arguments = listOf(navArgument("index") {
                                    type = NavType.StringType
                                })
                            ) {
                                val index = it.arguments?.getString("index")
                                selectedNavIndex = Screenpaths.ChatViewScreen
                                ChatView(viewmodel, index)
                            }

                            composable(Screenpaths.SettingsScreen.destination) {
                                selectedNavIndex = Screenpaths.SettingsScreen
                                SettingsScreen(viewmodel = viewmodel)
                            }

                            composable(Screenpaths.DebugScreen.destination) {
                                selectedNavIndex = Screenpaths.DebugScreen
                                DebugScreen(viewmodel = viewmodel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LuncheonNavigationBar(
    navHostController: NavHostController,
    selectedNavIndex: Screenpaths,
    setSelectedNavIndex: (Screenpaths) -> Unit,

) {
    val isLandscapeOrientation = when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> true
        else -> false
    }
    val haptics = LocalHapticFeedback.current
    val showNav = when (selectedNavIndex) {
        Screenpaths.AskPermissionsScreen,
        Screenpaths.ChatViewScreen,
        Screenpaths.DebugScreen -> false

        Screenpaths.ProbablySpamMessageScreen,
        Screenpaths.SettingsScreen -> true
    }

    if (showNav) {
        if (isLandscapeOrientation) {
            NavigationRail {
                navScreenList.map { screen ->
                    NavigationRailItem(
                        selected = screen == selectedNavIndex,
                        onClick = {
                            navHostController.navigate(screen.destination) {
                                this.popUpTo(selectedNavIndex.destination) {
                                    inclusive = true
                                }
                            }
//                        selectedNavIndex = screen
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.destination + " screen"
                            )
                        }
                    )
                }
            }
        } else {
            NavigationBar(
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        navHostController.navigate(Screenpaths.DebugScreen.destination)
                    }
                )
            ) {
                navScreenList.map { screen ->
                    NavigationBarItem(
                        selected = screen == selectedNavIndex,
                        onClick = {
                            navHostController.navigate(screen.destination) {
                                this.popUpTo(selectedNavIndex.destination) {
                                    inclusive = true
                                }
                            }
//                        selectedNavIndex = screen
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.destination + " screen"
                            )
                        },

                        )
                }
            }
        }
    }

}