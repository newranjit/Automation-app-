package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.SubApp
import com.example.ui.components.MiniOSActionCard
import com.example.ui.theme.MiniOSBgDark
import com.example.ui.theme.MiniOSCyan
import com.example.ui.theme.MiniOSEmerald
import com.example.ui.theme.MiniOSIndigo
import com.example.ui.theme.MiniOSRose
import com.example.ui.theme.MiniOSTextMuted
import com.example.ui.viewmodel.MiniOSViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiHubScreen(
    viewModel: MiniOSViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isProcessing by viewModel.isProcessingAI.collectAsState()
    val availableModels by viewModel.availableSlmModels.collectAsState()
    val activeModelId by viewModel.activeSlmModelId.collectAsState()
    val activeModel by viewModel.activeSlmModel.collectAsState()

    val gemma4Model = availableModels.find { it.id == "gemma_4_e2b_slm" }

    var inputText by remember { mutableStateOf("") }
    var isVoiceListening by remember { mutableStateOf(false) }
    var showSlmDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val quickChips = listOf(
        "🚀 Gemma 4 E2B" to "Activate Gemma 4 E2B",
        "🦙 Llama 3.2 128K" to "Activate Llama 3.2 128K",
        "⚡ Qwen 2.5 3B" to "Activate Qwen 2.5",
        "⏰ Alarm 7:00 AM" to "Set alarm for 7:00 AM",
        "☀️ Weather" to "What is the weather today?",
        "💧 Log Water" to "Log 1 glass of water",
        "📝 New Note" to "Take a note: Project milestones",
        "📁 Create File" to "Create file roadmap.txt",
        "🧮 20% tip on $95" to "Calculate 20% tip on $95",
        "⚡ Morning Briefing" to "Run morning briefing"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // SLM Engine Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val activeColor = when (activeModelId) {
                            "gemma_4_e2b_slm" -> MiniOSEmerald
                            "llama_3_2_3b_128k" -> MiniOSIndigo
                            "qwen_2_5_3b" -> MiniOSRose
                            else -> MiniOSCyan
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = activeColor.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showSlmDialog = true }
                                .testTag("active_slm_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = activeColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activeModel.name,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = activeColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Quick switch to other real models
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            availableModels.filter { it.id != activeModelId }.forEach { otherModel ->
                                val chipColor = when (otherModel.id) {
                                    "llama_3_2_3b_128k" -> MiniOSIndigo
                                    "qwen_2_5_3b" -> MiniOSRose
                                    else -> MiniOSEmerald
                                }
                                val label = when (otherModel.id) {
                                    "llama_3_2_3b_128k" -> "🦙 Llama 128K"
                                    "qwen_2_5_3b" -> "⚡ Qwen 2.5"
                                    else -> "🚀 Gemma 4"
                                }
                                OutlinedButton(
                                    onClick = { viewModel.activateSlmModel(otherModel.id) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(32.dp).testTag("quick_switch_${otherModel.id}")
                                ) {
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = chipColor)
                                }
                            }
                        }
                    }
                }
            }

            // Quick Action Prompt Chips Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickChips.forEach { (label, command) ->
                    SuggestionChip(
                        onClick = {
                            viewModel.sendChatPrompt(command)
                        },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("quick_chip_${label.take(6)}")
                    )
                }
            }

            // Conversational Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(
                        message = message,
                        onNavigateApp = { viewModel.switchApp(it) },
                        onEndCall = { viewModel.endCall() }
                    )
                }

                if (isProcessing) {
                    item {
                        GemmaThinkingIndicator()
                    }
                }
            }

            // Natural Language Command Input Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice Mic Button
                    IconButton(
                        onClick = { isVoiceListening = true },
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("ai_mic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Dictation",
                            tint = MiniOSCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Ask Gemma 2B or command any sub-app...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledIconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val textToSend = inputText
                                inputText = ""
                                viewModel.sendChatPrompt(textToSend)
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MiniOSCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("ai_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Command",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Voice Listening Dictation Modal Overlay
        if (isVoiceListening) {
            VoiceListeningOverlay(
                onDismiss = { isVoiceListening = false },
                onCommandSpoken = { cmd ->
                    isVoiceListening = false
                    viewModel.sendChatPrompt(cmd)
                }
            )
        }

        // On-Device SLM Model Manager Dialog
        if (showSlmDialog) {
            AlertDialog(
                onDismissRequest = { showSlmDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Memory, contentDescription = null, tint = MiniOSCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("On-Device SLM Engine")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "MiniOS executes all natural language understanding entirely on-device without any cloud telemetry.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        availableModels.forEach { model ->
                            val isCurrent = model.id == activeModelId
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrent) MiniOSCyan.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, MiniOSCyan) else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = model.name,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isCurrent) MiniOSCyan else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${model.sizeFormatted} • ${model.contextWindow} • ${model.parameterCount}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        if (isCurrent) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MiniOSEmerald
                                            ) {
                                                Text(
                                                    text = "ACTIVE",
                                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Black, fontWeight = FontWeight.Bold),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = model.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (model.status == com.example.data.model.SlmDownloadState.DOWNLOADING) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Downloading (${(model.downloadProgress * 100).toInt()}%)...",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MiniOSCyan
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        LinearProgressIndicator(
                                            progress = { model.downloadProgress.coerceIn(0f, 1f) },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                            color = MiniOSCyan,
                                            trackColor = MaterialTheme.colorScheme.surface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        when (model.status) {
                                            com.example.data.model.SlmDownloadState.NOT_DOWNLOADED -> {
                                                Button(
                                                    onClick = { viewModel.startSlmDownload(model.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MiniOSCyan, contentColor = Color.Black),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                    modifier = Modifier.testTag("dialog_download_btn_${model.id}")
                                                ) {
                                                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Download (${model.sizeFormatted})", fontSize = 11.sp)
                                                }
                                            }
                                            com.example.data.model.SlmDownloadState.DOWNLOADING -> {
                                                OutlinedButton(
                                                    onClick = { viewModel.cancelSlmDownload(model.id) },
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                    Text("Cancel", fontSize = 11.sp)
                                                }
                                            }
                                            com.example.data.model.SlmDownloadState.DOWNLOADED -> {
                                                Button(
                                                    onClick = {
                                                        viewModel.activateSlmModel(model.id)
                                                        showSlmDialog = false
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MiniOSEmerald, contentColor = Color.White),
                                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.testTag("dialog_activate_btn_${model.id}")
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Switch to ${model.name.split(" ").take(2).joinToString(" ")}", fontSize = 11.sp)
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
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Active Engine (Running Locally)", style = MaterialTheme.typography.labelSmall.copy(color = MiniOSEmerald, fontWeight = FontWeight.Bold))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSlmDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
private fun VoiceListeningOverlay(
    onDismiss: () -> Unit,
    onCommandSpoken: (String) -> Unit
) {
    val suggestedVoiceCommands = listOf(
        "Set alarm for 7:00 AM",
        "What is the weather today?",
        "Log 1 glass of water",
        "Take a photo",
        "Open browser"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MiniOSBgDark.copy(alpha = 0.96f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MiniOSCyan.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "⚡ MiniOS Neural Audio Dictation",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MiniOSCyan
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pulsing Core
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(MiniOSCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(MiniOSCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Listening",
                            tint = Color.Black,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Text(
                    text = "Listening for Command...",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = "Speak naturally or tap a quick spoken command below:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MiniOSTextMuted
                )

                // Quick Spoken Command Suggestions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestedVoiceCommands.forEach { cmd ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onCommandSpoken(cmd) },
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = MiniOSCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = "\"$cmd\"", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel Listening", color = Color.White)
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onNavigateApp: (SubApp) -> Unit,
    onEndCall: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        if (!message.isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MiniOSCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Gemma 2B",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MiniOSCyan
                    )
                )
                if (message.telemetry != null) {
                    Text(
                        text = " • ${message.telemetry.latencyMs}ms on-device",
                        style = MaterialTheme.typography.labelSmall,
                        color = MiniOSTextMuted
                    )
                }
            }
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) MiniOSIndigo else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = message.text.replace("**", ""),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        if (message.actionCard != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.widthIn(max = 330.dp)) {
                MiniOSActionCard(
                    actionCard = message.actionCard,
                    onNavigateApp = onNavigateApp,
                    onEndCall = onEndCall
                )
            }
        }
    }
}

@Composable
private fun GemmaThinkingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 6.dp, top = 4.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp,
            color = MiniOSCyan
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Gemma 2B executing on-device...",
            style = MaterialTheme.typography.labelSmall,
            color = MiniOSCyan
        )
    }
}
