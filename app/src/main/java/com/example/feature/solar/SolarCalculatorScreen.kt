package com.example.feature.solar

import androidx.compose.foundation.background
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
import com.example.data.local.room.SolarCalculationEntity
import com.example.ui.theme.*
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarCalculatorScreen(
    onSaveCalculation: (SolarCalculationEntity) -> Unit,
    onRequestInstall: () -> Unit
) {
    var buildingType by remember { mutableStateOf("منزل") }
    var consumptionKwhText by remember { mutableStateOf("450") }
    var peakSunHoursText by remember { mutableStateOf("5.5") }
    var availableAreaText by remember { mutableStateOf("120") }
    var showSavedDialog by remember { mutableStateOf(false) }

    val consumption = consumptionKwhText.toDoubleOrNull() ?: 450.0
    val peakHours = peakSunHoursText.toDoubleOrNull() ?: 5.5
    val dailyKwh = consumption / 30.0
    val systemKw = remember(dailyKwh, peakHours) { (dailyKwh / (peakHours * 0.75)).coerceAtLeast(1.0) }
    val panelsCount = remember(systemKw) { ceil((systemKw * 1000) / 550.0).toInt() }
    val batteryAh = remember(dailyKwh) { ceil((dailyKwh * 1000 * 1.3) / 24.0).toInt() }
    val inverterKw = remember(systemKw) { ceil(systemKw * 1.25 * 10) / 10.0 }
    val estimatedCost = remember(systemKw) { systemKw * 650000.0 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "حاسبة الطاقة الشمسية الهندسية",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AldawCharcoalDark
            )
            Text(
                text = "تقدير دقيق لحجم المنظومة وعدد الألواح والبطاريات وتكلفتها في السودان",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("نوع المنشأة", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("منزل", "مزرعة / مضخة", "منشأة تجارية").forEach { type ->
                            FilterChip(
                                selected = buildingType == type,
                                onClick = { buildingType = type },
                                label = { Text(type) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AldawOrange,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = consumptionKwhText,
                        onValueChange = { consumptionKwhText = it },
                        label = { Text("الاستهلاك الشهري (كيلوواط/ساعة - kWh)") },
                        trailingIcon = { Text("kWh", modifier = Modifier.padding(end = 12.dp), fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = peakSunHoursText,
                            onValueChange = { peakSunHoursText = it },
                            label = { Text("ساعات الذروة الشمسية") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = availableAreaText,
                            onValueChange = { availableAreaText = it },
                            label = { Text("المساحة المتاحة (م²)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AldawCharcoalDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.SolarPower, contentDescription = null, tint = AldawOrange)
                        Text(
                            "مواصفات المنظومة المقترحة",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    HorizontalDivider(color = AldawCharcoalLight)

                    SpecRow("قدرة المنظومة الموصى بها", "%.2f كيلوواط (kW)".format(systemKw), AldawOrangeLight)
                    SpecRow("عدد الألواح المطلوبة (550W)", "$panelsCount لوح شمسي مونوكريستال", Color.White)
                    SpecRow("سعة بنك البطاريات (24V)", "$batteryAh أمبير/ساعة (Ah)", Color.White)
                    SpecRow("قدرة الانفرتر الهجين (Hybrid)", "%.1f كيلوواط".format(inverterKw), Color.White)
                    SpecRow("التكلفة التقديرية الإجمالية", "%,.0f جنيه سوداني".format(estimatedCost), AldawOrange)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onSaveCalculation(
                                    SolarCalculationEntity(
                                        buildingType = buildingType,
                                        areaSqm = availableAreaText.toDoubleOrNull() ?: 120.0,
                                        monthlyConsumptionKwh = consumption,
                                        peakSunHours = peakHours,
                                        systemSizeKw = systemKw,
                                        panelsCount = panelsCount,
                                        batteryCapacityAh = batteryAh,
                                        inverterSizeKw = inverterKw,
                                        estimatedCost = estimatedCost
                                    )
                                )
                                showSavedDialog = true
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("حفظ الحسبة")
                        }

                        Button(
                            onClick = onRequestInstall,
                            colors = ButtonDefaults.buttonColors(containerColor = AldawOrange),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(Icons.Default.Engineering, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("طلب فني تركيب", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showSavedDialog) {
        AlertDialog(
            onDismissRequest = { showSavedDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AldawSolarGreen, modifier = Modifier.size(40.dp)) },
            title = { Text("تم حفظ المنظومة محلياً") },
            text = { Text("تم حفظ التقرير الهندسي بنجاح في قاعدة البيانات المحلية للرجوع إليها في أي وقت بدون اتصال.") },
            confirmButton = {
                Button(onClick = { showSavedDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AldawOrange)) {
                    Text("حسناً")
                }
            }
        )
    }
}

@Composable
fun SpecRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFFCBD5E1), fontSize = 13.sp)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
