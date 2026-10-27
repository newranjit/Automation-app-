package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MiniOSAmber
import com.example.ui.theme.MiniOSCyan
import com.example.ui.theme.MiniOSEmerald
import com.example.ui.viewmodel.MiniOSViewModel

@Composable
fun WeatherScreen(
    viewModel: MiniOSViewModel,
    modifier: Modifier = Modifier
) {
    val weather by viewModel.weatherInfo.collectAsState()
    val cities by viewModel.weatherCities.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isCelsius = settings.isCelsius

    fun formatT(tempC: Int): String {
        return if (isCelsius) "$tempC°C" else "${(tempC * 9 / 5) + 32}°F"
    }

    if (weather == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MiniOSCyan)
        }
        return
    }

    val info = weather!!

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Global Weather",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    // Unit Toggle (°C / °F)
                    FilledTonalButton(
                        onClick = { viewModel.toggleTempUnit() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("weather_temp_unit_toggle")
                    ) {
                        Text(
                            text = if (isCelsius) "Metric (°C)" else "Imperial (°F)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MiniOSCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // City Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cities.forEach { city ->
                        val isSelected = city == selectedCity
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectWeatherCity(city) },
                            label = { Text(city) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            modifier = Modifier.testTag("weather_city_chip_$city")
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Weather Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("weather_hero_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MiniOSCyan, modifier = Modifier.size(18.dp))
                            Text(
                                text = info.cityName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Icon(
                            imageVector = when {
                                info.condition.contains("Rain", ignoreCase = true) -> Icons.Default.WaterDrop
                                info.condition.contains("Cloud", ignoreCase = true) -> Icons.Default.Cloud
                                else -> Icons.Default.WbSunny
                            },
                            contentDescription = null,
                            tint = MiniOSAmber,
                            modifier = Modifier.size(68.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = formatT(info.tempC),
                            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold, color = MiniOSCyan)
                        )
                        Text(
                            text = info.condition,
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "High: ${formatT(info.highC)}  •  Low: ${formatT(info.lowC)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Hourly Forecast
            item {
                Text(
                    text = "Hourly Forecast",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    info.hourlyList.forEach { h ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.width(82.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = h.timeLabel, style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.height(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = MiniOSCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = formatT(h.tempC),
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // Environmental Metrics Grid (Atmosphere, Wind, UV, Sun)
            item {
                Text(
                    text = "Conditions & Radar Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricMiniCard(
                        title = "Humidity",
                        value = "${info.humidityPercent}%",
                        icon = Icons.Default.WaterDrop,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = "Wind Speed",
                        value = "${info.windSpeedKmh} km/h",
                        icon = Icons.Default.Air,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricMiniCard(
                        title = "Sunrise / Sunset",
                        value = "${info.sunrise} / ${info.sunset}",
                        icon = Icons.Default.WbTwilight,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = "Air Quality Index",
                        value = info.airQuality,
                        icon = Icons.Default.Eco,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricMiniCard(
                        title = "Barometer",
                        value = "${info.pressureHpa} hPa",
                        icon = Icons.Default.Compress,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = "Visibility",
                        value = "${info.visibilityKm} km",
                        icon = Icons.Default.Visibility,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 5-Day Forecast
            item {
                Text(
                    text = "5-Day Outlook",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(info.dailyList) { d ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = d.dayLabel, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(text = d.condition, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${formatT(d.maxTempC)} / ${formatT(d.minTempC)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MiniOSCyan)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = MiniOSCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}
