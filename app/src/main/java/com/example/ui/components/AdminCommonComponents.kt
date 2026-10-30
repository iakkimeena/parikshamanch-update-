package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppMode
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopAppBar(
    title: String,
    currentMode: AppMode,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onSwitchToStudent: () -> Unit,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        actions = {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ElectricBlue.copy(alpha = 0.2f),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                IconButton(onClick = onSwitchToStudent) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Switch to Student Portal",
                        tint = ElectricBlue
                    )
                }
            }
            IconButton(onClick = { onToggleDarkMode(!isDarkMode) }) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = "Toggle Dark Mode",
                    tint = Color.White
                )
            }
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color(0xFFEF4444)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NavyDark
        )
    )
}
