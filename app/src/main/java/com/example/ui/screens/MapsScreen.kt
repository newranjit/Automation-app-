package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MapPlace
import com.example.ui.theme.MiniOSBgDark
import com.example.ui.theme.MiniOSCyan
import com.example.ui.theme.MiniOSEmerald
import com.example.ui.theme.MiniOSIndigo
import com.example.ui.viewmodel.MiniOSViewModel

@Composable
fun MapsScreen(
    viewModel: MiniOSViewModel,
    modifier: Modifier = Modifier
) {
    val places by viewModel.places.collectAsState()
    val activeRoute by viewModel.activeRoute.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Coffee", "Work", "Food", "Medical")

    val filteredPlaces = places.filter {
        (selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)) &&
        (it.name.contains(searchQuery, ignoreCase = true) || it.address.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search & Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Maps & Navigation",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MiniOSEmerald.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "GPS Fixed • Offline Vector",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MiniOSEmerald
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search places, coffee, addresses...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Futuristic Vector Map Canvas HUD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MiniOSBgDark)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Road grid lines
                drawLine(Color(0xFF1E293B), Offset(0f, h * 0.4f), Offset(w, h * 0.4f), strokeWidth = 14f)
                drawLine(Color(0xFF1E293B), Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = 10f)
                drawLine(Color(0xFF1E293B), Offset(w * 0.35f, 0f), Offset(w * 0.35f, h), strokeWidth = 12f)
                drawLine(Color(0xFF1E293B), Offset(w * 0.75f, 0f), Offset(w * 0.75f, h), strokeWidth = 10f)

                // Navigation Route Polyline
                if (activeRoute != null) {
                    drawLine(
                        color = MiniOSCyan,
                        start = Offset(w * 0.35f, h * 0.75f),
                        end = Offset(w * 0.35f, h * 0.4f),
                        strokeWidth = 6f
                    )
                    drawLine(
                        color = MiniOSCyan,
                        start = Offset(w * 0.35f, h * 0.4f),
                        end = Offset(w * 0.65f, h * 0.4f),
                        strokeWidth = 6f
                    )
                    // Target marker
                    drawCircle(MiniOSEmerald, radius = 10f, center = Offset(w * 0.65f, h * 0.4f))
                }

                // User current location pulsing pin
                drawCircle(MiniOSCyan.copy(alpha = 0.3f), radius = 22f, center = Offset(w * 0.35f, h * 0.75f))
                drawCircle(MiniOSCyan, radius = 8f, center = Offset(w * 0.35f, h * 0.75f))
            }

            // HUD Overlay elements
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = MiniOSCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("N 37°46'29\"", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Route Card or Nearby Places
        if (activeRoute != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Routing to: ${activeRoute!!.name}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = "${activeRoute!!.distanceFormatted} • ${activeRoute!!.etaMinutes} min drive", style = MaterialTheme.typography.bodySmall, color = MiniOSCyan)
                    }
                    IconButton(onClick = { viewModel.selectRoute(null) }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel Route")
                    }
                }
            }
        }

        // Nearby Places List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            if (filteredPlaces.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No saved destinations",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Search an address or ask AI: 'Navigate to [Place]'",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(filteredPlaces, key = { it.id }) { place ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectRoute(place) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MiniOSIndigo),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (place.category) {
                                        "Coffee" -> Icons.Default.LocalCafe
                                        "Work" -> Icons.Default.Work
                                        "Food" -> Icons.Default.Restaurant
                                        else -> Icons.Default.Place
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = place.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                                Text(text = "${place.address} • ${place.distanceFormatted}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Button(
                            onClick = { viewModel.selectRoute(place) },
                            colors = ButtonDefaults.buttonColors(containerColor = MiniOSCyan, contentColor = Color.Black)
                        ) {
                            Text("Go (${place.etaMinutes}m)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
