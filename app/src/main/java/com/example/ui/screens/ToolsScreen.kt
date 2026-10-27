package com.example.ui.screens

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
import com.example.ui.theme.MiniOSAmber
import com.example.ui.theme.MiniOSCyan
import com.example.ui.theme.MiniOSEmerald
import com.example.ui.theme.MiniOSIndigo
import com.example.ui.theme.MiniOSRose
import com.example.ui.viewmodel.MiniOSViewModel

@Composable
fun ToolsScreen(
    viewModel: MiniOSViewModel,
    modifier: Modifier = Modifier
) {
    val calcExpr by viewModel.calcExpression.collectAsState()
    val calcRes by viewModel.calcResult.collectAsState()
    val calcHistory by viewModel.calcHistory.collectAsState()

    var selectedToolTab by remember { mutableStateOf(0) }
    val toolTabs = listOf("Calculator", "Converter", "Diagnostics")

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
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tools & Utilities",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MiniOSCyan.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Scientific & Hardware",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MiniOSCyan
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                TabRow(
                    selectedTabIndex = selectedToolTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MiniOSCyan
                ) {
                    toolTabs.forEachIndexed { index, name ->
                        Tab(
                            selected = selectedToolTab == index,
                            onClick = { selectedToolTab = index },
                            text = { Text(name, fontWeight = if (selectedToolTab == index) FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier.testTag("tools_tab_$index")
                        )
                    }
                }
            }
        }

        when (selectedToolTab) {
            0 -> CalculatorView(
                expression = calcExpr,
                result = calcRes,
                history = calcHistory,
                onKey = { viewModel.calcKeyPress(it) }
            )
            1 -> ConverterView()
            2 -> SystemDiagnosticsView()
        }
    }
}

@Composable
private fun CalculatorView(
    expression: String,
    result: String,
    history: List<String>,
    onKey: (String) -> Unit
) {
    var isScientificMode by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }

    val standardKeys = listOf(
        listOf("C", "(", ")", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "DEL", "=")
    )

    val scientificRow = listOf("sin", "cos", "tan", "√", "ln", "π")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display & Toolbar
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode Toggle
                FilterChip(
                    selected = isScientificMode,
                    onClick = { isScientificMode = !isScientificMode },
                    label = { Text(if (isScientificMode) "Scientific: ON" else "Scientific: OFF") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Functions,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.testTag("calc_scientific_toggle")
                )

                // History button
                TextButton(
                    onClick = { showHistorySheet = true },
                    modifier = Modifier.testTag("calc_history_btn")
                ) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp), tint = MiniOSCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tape (${history.size})", color = MiniOSCyan)
                }
            }

            // Calculation Display Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = if (expression.isNotBlank()) expression else "0",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = result,
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold, color = MiniOSCyan),
                        modifier = Modifier.testTag("calc_result_text")
                    )
                }
            }
        }

        // Scientific Row (if enabled)
        if (isScientificMode) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scientificRow.forEach { key ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onKey(key) }
                            .testTag("calc_key_$key"),
                        shape = RoundedCornerShape(10.dp),
                        color = MiniOSIndigo.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MiniOSCyan
                            )
                        }
                    }
                }
            }
        }

        // Standard Keypad
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            standardKeys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { key ->
                        val isOp = key in listOf("÷", "×", "-", "+", "=")
                        val isSpecial = key in listOf("C", "(", ")", "DEL")

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onKey(key) }
                                .testTag("calc_key_$key"),
                            shape = RoundedCornerShape(14.dp),
                            color = when {
                                key == "=" -> MiniOSEmerald
                                isOp -> MiniOSCyan.copy(alpha = 0.2f)
                                isSpecial -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = when {
                                        key == "=" -> Color.White
                                        isOp -> MiniOSCyan
                                        isSpecial -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showHistorySheet) {
        AlertDialog(
            onDismissRequest = { showHistorySheet = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = MiniOSCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Calculation History")
                }
            },
            text = {
                if (history.isEmpty()) {
                    Text("No calculations recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(history) { entry ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = entry,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistorySheet = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun ConverterView() {
    var inputAmount by remember { mutableStateOf("100") }
    var categoryIndex by remember { mutableStateOf(0) }
    val categories = listOf("Length", "Weight", "Temperature", "Digital Data")

    val valDouble = inputAmount.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEachIndexed { idx, name ->
                FilterChip(
                    selected = categoryIndex == idx,
                    onClick = { categoryIndex = idx },
                    label = { Text(name) }
                )
            }
        }

        OutlinedTextField(
            value = inputAmount,
            onValueChange = { inputAmount = it },
            label = { Text("Base Value") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Converted Results
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Converted Units",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                when (categoryIndex) {
                    0 -> { // Length: meters base
                        UnitRow("Kilometers (km)", "${valDouble / 1000} km")
                        UnitRow("Miles (mi)", "%.3f mi".format(valDouble * 0.000621371))
                        UnitRow("Feet (ft)", "%.2f ft".format(valDouble * 3.28084))
                        UnitRow("Inches (in)", "%.2f in".format(valDouble * 39.3701))
                    }
                    1 -> { // Weight: kg base
                        UnitRow("Pounds (lbs)", "%.2f lbs".format(valDouble * 2.20462))
                        UnitRow("Ounces (oz)", "%.2f oz".format(valDouble * 35.274))
                        UnitRow("Grams (g)", "${(valDouble * 1000).toLong()} g")
                        UnitRow("Tons (metric)", "%.4f t".format(valDouble / 1000.0))
                    }
                    2 -> { // Temp: Celsius base
                        UnitRow("Fahrenheit (°F)", "%.1f °F".format((valDouble * 9.0 / 5.0) + 32.0))
                        UnitRow("Kelvin (K)", "%.2f K".format(valDouble + 273.15))
                    }
                    3 -> { // Data: Gigabytes base
                        UnitRow("Megabytes (MB)", "${(valDouble * 1024).toLong()} MB")
                        UnitRow("Terabytes (TB)", "%.3f TB".format(valDouble / 1024.0))
                        UnitRow("Kilobytes (KB)", "${(valDouble * 1024 * 1024).toLong()} KB")
                    }
                }
            }
        }
    }
}

@Composable
private fun UnitRow(unit: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(unit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MiniOSCyan))
    }
}

@Composable
private fun SystemDiagnosticsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Memory, contentDescription = null, tint = MiniOSCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gemma 2B Neural NPU", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                DiagnosticStat("Model Weights", "4-bit Quantized INT4 (1.4 GB)")
                DiagnosticStat("Active RAM Allocation", "892 MB Resident")
                DiagnosticStat("Inference Latency", "18 ms / token")
                DiagnosticStat("Privacy Guarantee", "100% Air-Gapped Local Inference")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = MiniOSEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hardware Performance", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                DiagnosticStat("Battery Health", "96% (Good, 28°C)")
                DiagnosticStat("Available Storage", "64.2 GB / 128 GB")
                DiagnosticStat("Screen Refresh Rate", "120 Hz Dynamic")
                DiagnosticStat("System Architecture", "ARM64 v8.2-A")
            }
        }
    }
}

@Composable
private fun DiagnosticStat(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
    }
}
