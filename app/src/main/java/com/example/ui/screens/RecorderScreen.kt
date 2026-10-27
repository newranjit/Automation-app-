package com.example.ui.screens

import androidx.compose.animation.core.*
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
import com.example.ui.theme.MiniOSCyan
import com.example.ui.theme.MiniOSEmerald
import com.example.ui.theme.MiniOSIndigo
import com.example.ui.theme.MiniOSRose
import com.example.ui.viewmodel.MiniOSViewModel
import kotlinx.coroutines.delay

@Composable
fun RecorderScreen(
    viewModel: MiniOSViewModel,
    modifier: Modifier = Modifier
) {
    val memos by viewModel.audioMemos.collectAsState()
    var isRecording by remember { mutableStateOf(false) }
    var recordElapsedSeconds by remember { mutableStateOf(0) }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            recordElapsedSeconds += 1
        }
    }

    val mins = recordElapsedSeconds / 60
    val secs = recordElapsedSeconds % 60
    val recordTimerStr = "%02d:%02d".format(mins, secs)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
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
                    text = "Voice Recorder",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isRecording) MiniOSRose.copy(alpha = 0.2f) else MiniOSEmerald.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isRecording) "Recording..." else "Stereo 48kHz",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isRecording) MiniOSRose else MiniOSEmerald
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Live Audio Recorder HUD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isRecording) recordTimerStr else "Ready to Record",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isRecording) MiniOSRose else MiniOSCyan
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Simulated Waveform Bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(16) { i ->
                        val barHeight = if (isRecording) {
                            (12..46).random().dp
                        } else {
                            8.dp
                        }
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isRecording) MiniOSRose else MiniOSCyan.copy(alpha = 0.4f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Record / Stop Button
                FilledIconButton(
                    onClick = {
                        if (isRecording) {
                            val durationFormatted = "%02d:%02d".format(recordElapsedSeconds / 60, recordElapsedSeconds % 60)
                            viewModel.addAudioMemo("Voice Memo ${System.currentTimeMillis() % 1000}")
                            isRecording = false
                            recordElapsedSeconds = 0
                        } else {
                            isRecording = true
                            recordElapsedSeconds = 0
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (isRecording) MiniOSRose else MiniOSCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .size(68.dp)
                        .testTag("recorder_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Record",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Voice Memos List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Saved Audio Memos (${memos.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (memos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.MicNone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(52.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No voice memos recorded",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap the record button above to capture a memo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                items(memos, key = { it.id }) { memo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FilledIconButton(
                                    onClick = { viewModel.togglePlayMemo(memo.id) },
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = if (memo.isPlaying) MiniOSEmerald else MiniOSIndigo,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = if (memo.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = memo.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                                    Text(
                                        text = "${memo.durationFormatted} • ${memo.recordedDate} • ${memo.sizeFormatted}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.deleteMemo(memo.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }
}
