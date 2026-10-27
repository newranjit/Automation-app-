package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlarmItem
import com.example.ui.theme.MiniOSCyan
import com.example.ui.theme.MiniOSEmerald
import com.example.ui.theme.MiniOSIndigo
import com.example.ui.viewmodel.MiniOSViewModel
import kotlinx.coroutines.delay

@Composable
fun ClockScreen(
    viewModel: MiniOSViewModel,
    modifier: Modifier = Modifier
) {
    val alarms by viewModel.alarms.collectAsState()
    val worldClocks by viewModel.worldClocks.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Alarms", "World Clock", "Stopwatch", "Timer")
    var showAddAlarmDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddAlarmDialog = true },
                    containerColor = MiniOSCyan,
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("add_alarm_fab")
                ) {
                    Icon(Icons.Default.AddAlarm, contentDescription = "Add Alarm")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Clock",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MiniOSCyan.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "NTP Synced",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MiniOSCyan
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MiniOSCyan
                    ) {
                        tabs.forEachIndexed { index, name ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(name, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                }
            }

            when (selectedTab) {
                0 -> AlarmsTabView(
                    alarms = alarms,
                    onToggle = { viewModel.toggleAlarm(it) },
                    onDelete = { viewModel.deleteAlarm(it) }
                )
                1 -> WorldClockTabView(worldClocks = worldClocks)
                2 -> StopwatchTabView()
                3 -> TimerTabView()
            }
        }
    }

    if (showAddAlarmDialog) {
        var alarmTime by remember { mutableStateOf("7:00 AM") }
        var alarmLabel by remember { mutableStateOf("Wake Up") }

        AlertDialog(
            onDismissRequest = { showAddAlarmDialog = false },
            title = { Text("New Alarm") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = alarmTime,
                        onValueChange = { alarmTime = it },
                        label = { Text("Time (e.g. 7:00 AM)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = alarmLabel,
                        onValueChange = { alarmLabel = it },
                        label = { Text("Alarm Label") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (alarmTime.isNotBlank()) {
                            viewModel.addAlarm(alarmTime, alarmLabel.ifBlank { "Alarm" })
                            showAddAlarmDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAlarmDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AlarmsTabView(
    alarms: List<AlarmItem>,
    onToggle: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (alarms.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No alarms set",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + to set a wake-up alarm",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(alarms, key = { it.id }) { alarm ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = alarm.timeFormatted,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (alarm.isEnabled) MiniOSCyan else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${alarm.label} • ${alarm.repeatDays}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = alarm.isEnabled,
                            onCheckedChange = { onToggle(alarm.id) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = MiniOSCyan)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = { onDelete(alarm.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorldClockTabView(worldClocks: List<com.example.data.model.WorldClockItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(worldClocks, key = { it.id }) { clock ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = clock.cityName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = clock.timezoneOffset, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = clock.timeFormatted,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MiniOSCyan)
                    )
                }
            }
        }
    }
}

@Composable
private fun StopwatchTabView() {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }
    val laps = remember { mutableStateListOf<String>() }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            elapsedSeconds += 1
        }
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold, color = MiniOSCyan)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("High Precision On-Device Stopwatch", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Laps
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(laps) { lap ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = lap, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    elapsedSeconds = 0
                    isRunning = false
                    laps.clear()
                }
            ) {
                Text("Reset")
            }
            if (isRunning) {
                Button(
                    onClick = { laps.add("Lap ${laps.size + 1}: $formattedTime") },
                    colors = ButtonDefaults.buttonColors(containerColor = MiniOSIndigo)
                ) {
                    Text("Lap")
                }
            }
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) MiniOSEmerald else MiniOSCyan, contentColor = Color.Black)
            ) {
                Text(if (isRunning) "Pause" else "Start")
            }
        }
    }
}

@Composable
private fun TimerTabView() {
    var timerInitialSeconds by remember { mutableStateOf(300) }
    var timerSeconds by remember { mutableStateOf(300) }
    var isTimerActive by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerActive) {
        while (isTimerActive && timerSeconds > 0) {
            delay(1000)
            timerSeconds -= 1
        }
        if (timerSeconds == 0) isTimerActive = false
    }

    val mins = timerSeconds / 60
    val secs = timerSeconds % 60
    val timeFormatted = "%02d:%02d".format(mins, secs)
    val progress = if (timerInitialSeconds > 0) (timerSeconds.toFloat() / timerInitialSeconds).coerceIn(0f, 1f) else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(170.dp),
                    strokeWidth = 8.dp,
                    color = if (timerSeconds == 0) MiniOSCyan else MiniOSCyan,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, color = MiniOSCyan)
                    )
                    if (timerSeconds == 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MiniOSEmerald.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Time's Up! ⏰",
                                style = MaterialTheme.typography.labelSmall.copy(color = MiniOSEmerald, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Time Adjustment Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (timerSeconds >= 60) {
                            timerSeconds -= 60
                            timerInitialSeconds = timerSeconds
                        }
                    }
                ) {
                    Text("-1 min")
                }
                OutlinedButton(
                    onClick = {
                        timerSeconds += 60
                        timerInitialSeconds = timerSeconds
                    }
                ) {
                    Text("+1 min")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1 to "1m", 5 to "5m", 15 to "15m", 25 to "25m").forEach { (m, label) ->
                    FilledTonalButton(
                        onClick = {
                            timerInitialSeconds = m * 60
                            timerSeconds = m * 60
                            isTimerActive = false
                        }
                    ) {
                        Text(label)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = { timerSeconds = timerInitialSeconds; isTimerActive = false }) {
                Text("Reset")
            }
            Button(
                onClick = {
                    if (timerSeconds == 0) {
                        timerSeconds = timerInitialSeconds
                    }
                    isTimerActive = !isTimerActive
                },
                colors = ButtonDefaults.buttonColors(containerColor = MiniOSCyan, contentColor = Color.Black)
            ) {
                Text(if (isTimerActive) "Pause" else if (timerSeconds == 0) "Restart" else "Start Timer")
            }
        }
    }
}
