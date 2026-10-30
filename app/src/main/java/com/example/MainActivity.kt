package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.model.AppMode
import com.example.ui.AdminViewModel
import com.example.ui.screens.AdminControlCenterScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.StudentPanelScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AdMobHelper

class MainActivity : ComponentActivity() {

    private val viewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize Google Mobile Ads SDK (AdMob)
        AdMobHelper.initialize(this)
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ParikshaMainContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ParikshaMainContent(viewModel: AdminViewModel) {
    val appMode by viewModel.appMode.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (appMode) {
            AppMode.AUTH -> {
                AuthScreen(viewModel = viewModel)
            }
            AppMode.ADMIN_AUTH -> {
                AdminLoginScreen(viewModel = viewModel)
            }
            AppMode.ADMIN -> {
                AdminControlCenterScreen(viewModel = viewModel)
            }
            AppMode.STUDENT -> {
                StudentPanelScreen(viewModel = viewModel)
            }
        }
    }
}
