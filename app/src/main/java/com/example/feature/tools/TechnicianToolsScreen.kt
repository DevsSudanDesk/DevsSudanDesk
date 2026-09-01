package com.example.feature.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicianToolsScreen() {
    var selectedTool by remember { mutableStateOf(0) } // 0: Wire Gauge, 1: Voltage Drop, 2: Conversions

    // Wire gauge states
    var currentAmps by remember { mutableStateOf("30") }
    var distanceMeters by remember { mutableStateOf("25") }
    var metalType by remember { mutableStateOf("نحاس (Copper)") }

    val amps = currentAmps.toDoubleOrNull() ?: 30.0
    val distance = distanceMeters.toDoubleOrNull() ?: 25.0
    val resistivity = if (metalType.startsWith("نحاس")) 0.0175 else 0.028

    val recommendedGauge = remember(amps, distance, resistivity) {
        val dropAllowed = 220.0 * 0.03 // 3%
        val area = (2 * distance * amps * resistivity) / dropAllowed
        when {
            area <= 1.5 -> "1.5 مم²"
            area <= 2.5 -> "2.5 مم²"
            area <= 4.0 -> "4.0 مم²"
            area <= 6.0 -> "6.0 مم²"
            area <= 10.0 -> "10.0 مم²"
            area <= 16.0 -> "16.0 مم²"
            area <= 25.0 -> "25.0 مم²"
            else -> "35.0+ مم² (كابل رئيسي)"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "حقيبة أدوات الفني والمهندس",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AldawCharcoalDark
            )
            Text(
                text = "حسابات المقاطع وهبوط الجهد والتحويلات الكهربائية الميدانية",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            PrimaryTabRow(selectedTabIndex = selectedTool) {
                Tab(
                    selected = selectedTool == 0,
                    onClick = { selectedTool = 0 },
                    text = { Text("مقطع السلك", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTool == 1,
                    onClick = { selectedTool = 1 },
                    text = { Text("هبوط الجهد", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTool == 2,
                    onClick = { selectedTool = 2 },
                    text = { Text("التحويلات", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTool == 0) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = AldawOrangeDark)
                            Text("حاسبة مقطع الكابل وفق الكود الدولي", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        OutlinedTextField(
                            value = currentAmps,
                            onValueChange = { currentAmps = it },
                            label = { Text("شدة التيار (أمبير - Amps)") },
                            trailingIcon = { Text("A", modifier = Modifier.padding(end = 12.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = distanceMeters,
                            onValueChange = { distanceMeters = it },
                            label = { Text("طول المسار (متر)") },
                            trailingIcon = { Text("m", modifier = Modifier.padding(end = 12.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("المادة الموصلة:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("نحاس (Copper)", "ألومنيوم (Aluminum)").forEach { metal ->
                                FilterChip(
                                    selected = metalType == metal,
                                    onClick = { metalType = metal },
                                    label = { Text(metal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AldawOrange,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = AldawOrangeLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("المقطع القياسي الأدنى المطلوب:", fontSize = 12.sp, color = AldawCharcoalDark)
                                Text(recommendedGauge, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AldawOrangeDark)
                                Text("نسبة هبوط الجهد محسوبة على ألا تتجاوز 3%", fontSize = 11.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        } else if (selectedTool == 1) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("قواعد هبوط الجهد للألواح والبطاريات", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("• الحد الأقصى المسموح به لهبوط الجهد بين الألواح والإنفرتر: 2% - 3%", fontSize = 13.sp)
                        Text("• هبوط الجهد بين البطاريات والإنفرتر (DC Cable) يجب ألا يتجاوز 1%", fontSize = 13.sp)
                        Text("• قانون حساب هبوط الجهد: V_drop = (2 × L × I × ρ) ÷ A", fontSize = 13.sp, color = AldawOrangeDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("دليل التحويلات الكهربائية السريعة", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("• 1 حصان ميكانيكي (HP) = 746 واط (Watt)", fontSize = 13.sp)
                        Text("• 1 كيلوواط (kW) = 1.34 حصان (HP)", fontSize = 13.sp)
                        Text("• طاقة البطارية بالكيلوواط = (الجهد بالفولت × السعة بالأمبير/ساعة) ÷ 1000", fontSize = 13.sp)
                        Text("• تيار الحمل الأحادي (220V) ≈ القدرة بالواط ÷ 220 ÷ معامل القدرة (0.85)", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
