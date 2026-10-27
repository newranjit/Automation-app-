package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubApp
import com.example.ui.theme.MiniOSCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniOSDock(
    currentApp: SubApp,
    onAppSelect: (SubApp) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAppDrawer by remember { mutableStateOf(false) }

    // Primary dock items
    val primaryDockApps = listOf(
        SubApp.AI_HUB,
        SubApp.PHONE,
        SubApp.MESSAGES,
        SubApp.CALENDAR,
        SubApp.BROWSER,
        SubApp.FILES,
        SubApp.CLOCK,
        SubApp.WEATHER
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        val scrollState = rememberScrollState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // "All Apps" launcher grid button
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showAppDrawer = true }
                    .background(if (showAppDrawer) MiniOSCyan.copy(alpha = 0.2f) else Color.Transparent)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("dock_all_apps_btn"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = "All Apps",
                        tint = MiniOSCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Apps",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                    color = MiniOSCyan
                )
            }

            // Sub-apps list
            SubApp.values().forEach { app ->
                val isSelected = currentApp == app
                val icon = getAppIcon(app)

                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) MiniOSCyan.copy(alpha = 0.18f) else Color.Transparent,
                    label = "dock_bg_anim"
                )
                val tintColor by animateColorAsState(
                    targetValue = if (isSelected) MiniOSCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "dock_tint_anim"
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onAppSelect(app) }
                        .background(backgroundColor)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("dock_app_${app.name.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = app.label,
                            tint = tintColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = tintColor
                    )
                }
            }
        }
    }

    // All Apps Drawer Modal Bottom Sheet
    if (showAppDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showAppDrawer = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MiniOS App Library",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MiniOSCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "17 Built-In Apps",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MiniOSCyan
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(SubApp.values()) { app ->
                        val isSelected = currentApp == app
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onAppSelect(app)
                                    showAppDrawer = false
                                }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) MiniOSCyan else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getAppIcon(app),
                                    contentDescription = app.label,
                                    tint = if (isSelected) Color.Black else Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = app.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getAppIcon(app: SubApp): ImageVector {
    return when (app) {
        SubApp.AI_HUB -> Icons.Default.SmartToy
        SubApp.PHONE -> Icons.Default.Phone
        SubApp.MESSAGES -> Icons.Default.Message
        SubApp.CALENDAR -> Icons.Default.CalendarMonth
        SubApp.BROWSER -> Icons.Default.Public
        SubApp.FILES -> Icons.Default.Folder
        SubApp.NOTES -> Icons.Default.EditNote
        SubApp.TOOLS -> Icons.Default.Calculate
        SubApp.AUTOMATIONS -> Icons.Default.AutoMode
        SubApp.CLOCK -> Icons.Default.Alarm
        SubApp.WEATHER -> Icons.Default.Cloud
        SubApp.GALLERY -> Icons.Default.PhotoLibrary
        SubApp.CAMERA -> Icons.Default.PhotoCamera
        SubApp.RECORDER -> Icons.Default.Mic
        SubApp.MAPS -> Icons.Default.Map
        SubApp.HEALTH -> Icons.Default.Favorite
        SubApp.SETTINGS -> Icons.Default.Settings
    }
}
