package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MiniOSCyan
import com.example.ui.theme.MiniOSEmerald
import com.example.ui.theme.MiniOSIndigo
import com.example.ui.viewmodel.MiniOSViewModel

@Composable
fun SettingsScreen(
    viewModel: MiniOSViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MiniOSCyan.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "System Core",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MiniOSCyan
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // OS Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = MiniOSCyan, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "MiniOS 2.0 LTS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Neural Core: On-Device Gemma 2B (INT4 Quantized)", style = MaterialTheme.typography.bodySmall)
                        Text("• Security: 100% Air-Gapped Local Execution", style = MaterialTheme.typography.bodySmall, color = MiniOSEmerald)
                        Text("• Total Sub-Apps: 17 Native Unified Apps", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // AI Neural Engine & SLM Model Manager
            item {
                val availableModels by viewModel.availableSlmModels.collectAsState()
                val activeModelId by viewModel.activeSlmModelId.collectAsState()

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("slm_manager_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Memory, contentDescription = null, tint = MiniOSCyan, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("On-Device SLM Models", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MiniOSEmerald.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "100% Local NPU",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MiniOSEmerald, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Download and toggle offline Small Language Models (SLMs) to power MiniOS phone automations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Render both SLM models
                        availableModels.forEach { model ->
                            val isCurrent = model.id == activeModelId

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isCurrent) MiniOSCyan.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                                border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, MiniOSCyan) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .testTag("model_item_${model.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = model.name,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (isCurrent) MiniOSCyan else MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = MaterialTheme.colorScheme.surfaceVariant
                                                ) {
                                                    Text(
                                                        text = model.tag,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${model.sizeFormatted} • ${model.contextWindow} • ${model.parameterCount} • ${model.quantization}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        if (isCurrent) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MiniOSEmerald
                                            ) {
                                                Text(
                                                    text = "ACTIVE",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = model.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Download Progress Bar if downloading
                                    if (model.status == com.example.data.model.SlmDownloadState.DOWNLOADING) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        val mbTotal = (model.sizeBytes / (1024 * 1024)).toInt()
                                        val mbDownloaded = (model.downloadProgress * mbTotal).toInt()
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Downloading model weights (${(model.downloadProgress * 100).toInt()}%)...",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MiniOSCyan
                                            )
                                            Text(
                                                text = "$mbDownloaded MB / $mbTotal MB",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { model.downloadProgress.coerceIn(0f, 1f) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = MiniOSCyan,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Action buttons for this SLM model
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        when (model.status) {
                                            com.example.data.model.SlmDownloadState.NOT_DOWNLOADED -> {
                                                Button(
                                                    onClick = { viewModel.startSlmDownload(model.id) },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MiniOSCyan,
                                                        contentColor = Color.Black
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.testTag("download_btn_${model.id}")
                                                ) {
                                                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Download (${model.sizeFormatted})")
                                                }
                                            }
                                            com.example.data.model.SlmDownloadState.DOWNLOADING -> {
                                                OutlinedButton(
                                                    onClick = { viewModel.cancelSlmDownload(model.id) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.testTag("cancel_download_btn_${model.id}")
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Cancel Download")
                                                }
                                            }
                                            com.example.data.model.SlmDownloadState.DOWNLOADED -> {
                                                Button(
                                                    onClick = { viewModel.activateSlmModel(model.id) },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = MiniOSEmerald,
                                                        contentColor = Color.White
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.testTag("activate_btn_${model.id}")
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Switch to ${model.name.split(" ").take(2).joinToString(" ")}")
                                                }
                                            }
                                            com.example.data.model.SlmDownloadState.ACTIVE -> {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MiniOSEmerald.copy(alpha = 0.2f)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MiniOSEmerald, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("Active Intelligence Engine", style = MaterialTheme.typography.labelSmall.copy(color = MiniOSEmerald, fontWeight = FontWeight.Bold))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Turbo Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Gemma Turbo NPU Mode", fontWeight = FontWeight.SemiBold)
                                Text("Prioritize GPU/NPU acceleration for ~22ms latency", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = settings.isGemmaTurboMode,
                                onCheckedChange = { viewModel.toggleTurboMode() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = MiniOSCyan)
                            )
                        }
                    }
                }
            }

            // Display & Theme
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Display & Appearance", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Dark Cyber-Slate Theme", fontWeight = FontWeight.SemiBold)
                                Text("High contrast OLED power saving", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = settings.isDarkTheme,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = MiniOSCyan)
                            )
                        }
                    }
                }
            }

            // Connectivity
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Network & Radios", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Wifi, contentDescription = null, tint = MiniOSCyan)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Wi-Fi 6E")
                            }
                            Switch(
                                checked = settings.isWifiEnabled,
                                onCheckedChange = { viewModel.updateSettings(settings.copy(isWifiEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = MiniOSCyan)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bluetooth, contentDescription = null, tint = MiniOSIndigo)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Bluetooth 5.3")
                            }
                            Switch(
                                checked = settings.isBluetoothEnabled,
                                onCheckedChange = { viewModel.updateSettings(settings.copy(isBluetoothEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = MiniOSCyan)
                            )
                        }
                    }
                }
            }

            // Chat & Cache actions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Data & Privacy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.repository.clearChat() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clear Chat Thread History")
                        }
                    }
                }
            }
        }
    }
}
