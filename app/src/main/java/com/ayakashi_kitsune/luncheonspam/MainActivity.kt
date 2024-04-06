package com.ayakashi_kitsune.luncheonspam

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ayakashi_kitsune.luncheonspam.backbone.LuncheonViewmodel
import com.ayakashi_kitsune.luncheonspam.backbone.Screenpaths
import com.ayakashi_kitsune.luncheonspam.features.AskPermission.ui.AskPermissionsScreen
import com.ayakashi_kitsune.luncheonspam.features.NotificationService.NotificationService
import com.ayakashi_kitsune.luncheonspam.features.ProbablySpamMessage.ui.ProbablySpamMessageUI
import com.ayakashi_kitsune.luncheonspam.features.contentProvider.core.ContentReceiver
import com.ayakashi_kitsune.luncheonspam.ui.theme.LuncheonSpamTheme

class LuncheonSpamApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val notificationService = NotificationService(this)
        notificationService.createChannel()
    }
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LuncheonSpamTheme {
                val viewmodel = viewModel<LuncheonViewmodel>()

                val contentReceiver = ContentReceiver(this)

                val navHostController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
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
                                        applicationContext.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                                    )
                                } else {
                                    /*
                                    * when the device is lower than android 13
                                    * */
                                    listOf<Boolean>(
                                        applicationContext.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED,
                                        applicationContext.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED,
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
                            }
                            /* else just show the ask perissions screen*/
                            AskPermissionsScreen(navHostController = navHostController)
                        }

                        composable(Screenpaths.ProbablySpamMessageScreen.destination,
                            enterTransition = { slideInHorizontally() }) {
                            ProbablySpamMessageUI(
                                viewmodel = viewmodel,
                                contentReceiver = contentReceiver,
                            )
                        }
                    }
                }
            }
        }
    }


}