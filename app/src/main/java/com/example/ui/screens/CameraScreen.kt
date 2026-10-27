package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.SubApp
import com.example.ui.theme.MiniOSAmber
import com.example.ui.theme.MiniOSBgDark
import com.example.ui.theme.MiniOSCyan
import com.example.ui.theme.MiniOSEmerald
import com.example.ui.viewmodel.MiniOSViewModel
import kotlinx.coroutines.delay

@Composable
fun CameraScreen(
    viewModel: MiniOSViewModel,
    modifier: Modifier = Modifier
) {
    var isFrontCamera by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isHdrOn by remember { mutableStateOf(true) }
    var showFlashEffect by remember { mutableStateOf(false) }
    var captureSuccessMessage by remember { mutableStateOf("") }

    LaunchedEffect(showFlashEffect) {
        if (showFlashEffect) {
            delay(120)
            showFlashEffect = false
        }
    }

    LaunchedEffect(captureSuccessMessage) {
        if (captureSuccessMessage.isNotEmpty()) {
            delay(2500)
            captureSuccessMessage = ""
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MiniOSBgDark)
    ) {
        // Simulated Camera Viewfinder with High-Tech OS HUD
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Viewfinder Top Controls
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isFlashOn = !isFlashOn }) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = if (isFlashOn) MiniOSAmber else Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isFrontCamera) "FRONT 32MP • f/2.2" else "MAIN 50MP • OIS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MiniOSCyan
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = { isHdrOn = !isHdrOn }) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isHdrOn) MiniOSCyan else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                        ) {
                            Text(
                                text = "HDR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHdrOn) Color.Black else Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Viewfinder Center HUD (Crosshairs & Composition lines)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Focus crosshair
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(1.5.dp, MiniOSCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                )

                if (captureSuccessMessage.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MiniOSEmerald.copy(alpha = 0.9f),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = captureSuccessMessage,
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.Black, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Bottom Shutter Controls
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery Shortcut
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.switchApp(SubApp.GALLERY) }
                            .testTag("camera_to_gallery_btn"),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = MiniOSCyan)
                        }
                    }

                    // Main Shutter Button
                    Surface(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .clickable {
                                showFlashEffect = true
                                val name = "IMG_${System.currentTimeMillis() % 10000}.jpg"
                                viewModel.addPhoto(name)
                                captureSuccessMessage = "Captured $name"
                            }
                            .testTag("camera_shutter_btn"),
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(4.dp, MiniOSCyan)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }

                    // Camera Switch (Front/Back)
                    IconButton(
                        onClick = { isFrontCamera = !isFrontCamera },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "Switch Camera",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Shutter Flash overlay
        if (showFlashEffect) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.85f))
            )
        }
    }
}
