package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionCard
import com.example.data.model.CallStatus
import com.example.data.model.SubApp
import com.example.ui.theme.*

@Composable
fun MiniOSActionCard(
    actionCard: ActionCard,
    onNavigateApp: (SubApp) -> Unit,
    onEndCall: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        when (actionCard) {
            is ActionCard.CallCard -> CallCardContent(actionCard, onNavigateApp, onEndCall)
            is ActionCard.MessageCard -> MessageCardContent(actionCard, onNavigateApp)
            is ActionCard.CalendarCard -> CalendarCardContent(actionCard, onNavigateApp)
            is ActionCard.BrowserCard -> BrowserCardContent(actionCard, onNavigateApp)
            is ActionCard.FileCard -> FileCardContent(actionCard, onNavigateApp)
            is ActionCard.CalcCard -> CalcCardContent(actionCard)
            is ActionCard.AutomationCard -> AutomationCardContent(actionCard)
            is ActionCard.AlarmCard -> AlarmCardContent(actionCard, onNavigateApp)
            is ActionCard.WeatherCard -> WeatherCardContent(actionCard, onNavigateApp)
            is ActionCard.HealthCard -> HealthCardContent(actionCard, onNavigateApp)
            is ActionCard.MapRouteCard -> MapRouteCardContent(actionCard, onNavigateApp)
            is ActionCard.SlmModelCard -> SlmModelCardContent(actionCard, onNavigateApp)
        }
    }
}

@Composable
private fun CallCardContent(
    card: ActionCard.CallCard,
    onNavigateApp: (SubApp) -> Unit,
    onEndCall: () -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (card.status == CallStatus.CONNECTED) MiniOSEmerald else MiniOSRose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneInTalk,
                        contentDescription = "Call",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = card.contactName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = card.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = if (card.status == CallStatus.CONNECTED) MiniOSEmerald.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (card.status == CallStatus.CONNECTED) "Active Call" else "Call Ended",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (card.status == CallStatus.CONNECTED) MiniOSEmerald else MiniOSTextMuted,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (card.status == CallStatus.CONNECTED) {
                Button(
                    onClick = onEndCall,
                    colors = ButtonDefaults.buttonColors(containerColor = MiniOSRose),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("action_card_end_call")
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "End Call", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("End Call", fontSize = 12.sp)
                }
            }
            OutlinedButton(
                onClick = { onNavigateApp(SubApp.PHONE) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_card_open_phone")
            ) {
                Icon(Icons.Default.Dialpad, contentDescription = "Open Phone", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Phone", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun MessageCardContent(
    card: ActionCard.MessageCard,
    onNavigateApp: (SubApp) -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MiniOSIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "SMS", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "To: ${card.recipientName}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = card.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                color = MiniOSCyan.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "SMS Sent",
                    style = MaterialTheme.typography.labelSmall.copy(color = MiniOSCyan, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = card.messageBody,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = { onNavigateApp(SubApp.MESSAGES) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_card_open_messages")
        ) {
            Icon(Icons.Default.ArrowOutward, contentDescription = "Open", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open Conversation", fontSize = 12.sp)
        }
    }
}

@Composable
private fun CalendarCardContent(
    card: ActionCard.CalendarCard,
    onNavigateApp: (SubApp) -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MiniOSAmber),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Event, contentDescription = "Event", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = card.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = "${card.date} • ${card.time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                color = MiniOSAmber.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = card.category,
                    style = MaterialTheme.typography.labelSmall.copy(color = MiniOSAmber, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onNavigateApp(SubApp.CALENDAR) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_card_open_calendar"),
            colors = ButtonDefaults.buttonColors(containerColor = MiniOSCyan.copy(alpha = 0.25f), contentColor = MiniOSCyan)
        ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("View in Calendar", fontSize = 12.sp)
        }
    }
}

@Composable
private fun BrowserCardContent(
    card: ActionCard.BrowserCard,
    onNavigateApp: (SubApp) -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MiniOSCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Language, contentDescription = "Web", tint = Color.Black, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = card.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = card.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
        }
        if (card.summary.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = card.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onNavigateApp(SubApp.BROWSER) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_card_open_browser")
        ) {
            Icon(Icons.Default.OpenInBrowser, contentDescription = "Browse", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open in MiniOS Browser", fontSize = 12.sp)
        }
    }
}

@Composable
private fun FileCardContent(
    card: ActionCard.FileCard,
    onNavigateApp: (SubApp) -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MiniOSIndigo),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = "File", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = card.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = "${card.path} • ${card.sizeFormatted}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = { onNavigateApp(SubApp.FILES) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_card_open_files")
        ) {
            Icon(Icons.Default.FolderOpen, contentDescription = "Open Files", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("View in File Manager", fontSize = 12.sp)
        }
    }
}

@Composable
private fun CalcCardContent(card: ActionCard.CalcCard) {
    Column(modifier = Modifier.padding(14.dp)) {
        Text(
            text = "Calculation Result",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = card.result,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MiniOSCyan)
        )
        if (card.steps.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.steps,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AutomationCardContent(card: ActionCard.AutomationCard) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = MiniOSEmerald)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = card.routineName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            Surface(
                color = MiniOSEmerald.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Executed On-Device",
                    style = MaterialTheme.typography.labelSmall.copy(color = MiniOSEmerald, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        card.steps.forEach { step ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MiniOSEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = step, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun AlarmCardContent(
    card: ActionCard.AlarmCard,
    onNavigateApp: (SubApp) -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MiniOSCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Alarm, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = card.time, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = card.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Surface(
                color = MiniOSEmerald.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (card.isEnabled) "Alarm Set" else "Disabled",
                    style = MaterialTheme.typography.labelSmall.copy(color = MiniOSEmerald, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onNavigateApp(SubApp.CLOCK) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AlarmOn, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open Clock & Alarms", fontSize = 12.sp)
        }
    }
}

@Composable
private fun WeatherCardContent(
    card: ActionCard.WeatherCard,
    onNavigateApp: (SubApp) -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MiniOSAmber),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = card.city, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(text = card.highLow, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                text = "${card.tempC}°C",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MiniOSCyan)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onNavigateApp(SubApp.WEATHER) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Cloud, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("View Full Weather Forecast", fontSize = 12.sp)
        }
    }
}

@Composable
private fun HealthCardContent(
    card: ActionCard.HealthCard,
    onNavigateApp: (SubApp) -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MiniOSEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = "${card.steps} Steps", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = card.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Surface(
                color = MiniOSEmerald.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "${card.waterGlasses} 💧",
                    style = MaterialTheme.typography.labelSmall.copy(color = MiniOSEmerald, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onNavigateApp(SubApp.HEALTH) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DirectionsRun, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open Health & Fitness", fontSize = 12.sp)
        }
    }
}

@Composable
private fun MapRouteCardContent(
    card: ActionCard.MapRouteCard,
    onNavigateApp: (SubApp) -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MiniOSIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = card.destination, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = card.routeSummary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Surface(
                color = MiniOSCyan.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "${card.etaMinutes} mins",
                    style = MaterialTheme.typography.labelSmall.copy(color = MiniOSCyan, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onNavigateApp(SubApp.MAPS) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Open Maps & Navigation", fontSize = 12.sp)
        }
    }
}

@Composable
private fun SlmModelCardContent(
    card: ActionCard.SlmModelCard,
    onNavigateApp: (SubApp) -> Unit
) {
    Column(modifier = Modifier.padding(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MiniOSCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = MiniOSCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = card.modelName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${card.sizeFormatted} • ${card.contextWindow} • ${card.version}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (card.status) {
                    com.example.data.model.SlmDownloadState.ACTIVE -> MiniOSEmerald.copy(alpha = 0.2f)
                    com.example.data.model.SlmDownloadState.DOWNLOADING -> MiniOSIndigo.copy(alpha = 0.2f)
                    com.example.data.model.SlmDownloadState.DOWNLOADED -> MiniOSCyan.copy(alpha = 0.2f)
                    com.example.data.model.SlmDownloadState.NOT_DOWNLOADED -> MaterialTheme.colorScheme.surface
                }
            ) {
                Text(
                    text = when (card.status) {
                        com.example.data.model.SlmDownloadState.ACTIVE -> "ACTIVE"
                        com.example.data.model.SlmDownloadState.DOWNLOADING -> "${(card.progress * 100).toInt()}%"
                        com.example.data.model.SlmDownloadState.DOWNLOADED -> "DOWNLOADED"
                        com.example.data.model.SlmDownloadState.NOT_DOWNLOADED -> "AVAILABLE"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = when (card.status) {
                            com.example.data.model.SlmDownloadState.ACTIVE -> MiniOSEmerald
                            com.example.data.model.SlmDownloadState.DOWNLOADING -> MiniOSIndigo
                            com.example.data.model.SlmDownloadState.DOWNLOADED -> MiniOSCyan
                            com.example.data.model.SlmDownloadState.NOT_DOWNLOADED -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = card.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (card.status == com.example.data.model.SlmDownloadState.DOWNLOADING) {
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { card.progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MiniOSCyan,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { onNavigateApp(SubApp.SETTINGS) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MiniOSCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Manage in Settings")
            }
        }
    }
}
