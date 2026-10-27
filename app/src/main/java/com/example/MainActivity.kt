package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.SubApp
import com.example.ui.components.MiniOSDock
import com.example.ui.components.MiniOSTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MiniOSCyan
import com.example.ui.theme.MiniOSEmerald
import com.example.ui.theme.MiniOSIndigo
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MiniOSViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: MiniOSViewModel = viewModel()
            val settings by vm.settings.collectAsState()
            MyApplicationTheme(darkTheme = settings.isDarkTheme) {
                MiniOSApp(viewModel = vm)
            }
        }
    }
}

@Composable
fun MiniOSApp(viewModel: MiniOSViewModel = viewModel()) {
    val currentApp by viewModel.currentApp.collectAsState()
    val isAiProcessing by viewModel.isProcessingAI.collectAsState()
    var showGemmaInfoDialog by remember { mutableStateOf(false) }

    // Android back navigation: any sub-app returns to AI Hub
    BackHandler(enabled = currentApp != SubApp.AI_HUB) {
        viewModel.switchApp(SubApp.AI_HUB)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MiniOSTopBar(
                isAiProcessing = isAiProcessing,
                onGemmaBadgeClick = { showGemmaInfoDialog = true }
            )
        },
        bottomBar = {
            MiniOSDock(
                currentApp = currentApp,
                onAppSelect = { app -> viewModel.switchApp(app) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentApp,
                label = "app_switch_crossfade"
            ) { app ->
                when (app) {
                    SubApp.AI_HUB -> AiHubScreen(viewModel = viewModel)
                    SubApp.PHONE -> PhoneScreen(viewModel = viewModel)
                    SubApp.MESSAGES -> MessagesScreen(viewModel = viewModel)
                    SubApp.CALENDAR -> CalendarScreen(viewModel = viewModel)
                    SubApp.BROWSER -> BrowserScreen(viewModel = viewModel)
                    SubApp.FILES -> FileManagerScreen(viewModel = viewModel)
                    SubApp.NOTES -> NotesScreen(viewModel = viewModel)
                    SubApp.TOOLS -> ToolsScreen(viewModel = viewModel)
                    SubApp.AUTOMATIONS -> AutomationsScreen(viewModel = viewModel)
                    SubApp.CLOCK -> ClockScreen(viewModel = viewModel)
                    SubApp.WEATHER -> WeatherScreen(viewModel = viewModel)
                    SubApp.GALLERY -> GalleryScreen(viewModel = viewModel)
                    SubApp.CAMERA -> CameraScreen(viewModel = viewModel)
                    SubApp.RECORDER -> RecorderScreen(viewModel = viewModel)
                    SubApp.MAPS -> MapsScreen(viewModel = viewModel)
                    SubApp.HEALTH -> HealthScreen(viewModel = viewModel)
                    SubApp.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }

            // Floating Quick-Command Assistant Pill when inside any sub-app
            if (currentApp != SubApp.AI_HUB) {
                FloatingActionButton(
                    onClick = { viewModel.switchApp(SubApp.AI_HUB) },
                    containerColor = MiniOSCyan,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Hub", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showGemmaInfoDialog) {
        AlertDialog(
            onDismissRequest = { showGemmaInfoDialog = false },
            icon = {
                Icon(Icons.Default.Speed, contentDescription = null, tint = MiniOSCyan, modifier = Modifier.size(32.dp))
            },
            title = {
                Text(
                    text = "Gemma 2B Architecture",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "MiniOS operates on a local 2.0B parameter model optimized for on-device phone orchestration and privacy.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = MiniOSEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("100% Private & Air-Gapped", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Text("No telemetry, audio, or message data ever leaves your device.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = MiniOSCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ultra Low Latency (~38ms)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Text("Instant intent extraction and tool execution across all 17 sub-apps.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showGemmaInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MiniOSCyan, contentColor = Color.Black)
                ) {
                    Text("Got it")
                }
            }
        )
    }
}
