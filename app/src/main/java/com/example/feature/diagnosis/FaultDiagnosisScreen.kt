package com.example.feature.diagnosis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.FaultItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaultDiagnosisScreen(
    faults: List<FaultItem>,
    onRequestTechnician: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("الكل") }
    var selectedFault by remember { mutableStateOf<FaultItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("الكل", "طاقة شمسية", "كهرباء منزلية", "بطاريات وتخزين")

    val filteredList = faults.filter { fault ->
        val matchesCategory = (selectedCategory == "الكل" || fault.category == selectedCategory)
        val matchesSearch = searchQuery.isEmpty() ||
                fault.title.contains(searchQuery, ignoreCase = true) ||
                fault.symptoms.contains(searchQuery, ignoreCase = true) ||
                fault.errorCode.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "دليل تشخيص الأعطال الميداني",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AldawCharcoalDark
            )
            Text(
                text = "افحص الأعراض ورموز الأخطاء الشائعة لمعرفة الأسباب وطرق السلامة الوقائية",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث عن العطل أو رمز الخطأ...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AldawOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        items(filteredList) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedFault = item }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                if (item.isCritical) Icons.Default.WarningAmber else Icons.Default.BuildCircle,
                                contentDescription = null,
                                tint = if (item.isCritical) Color.Red else AldawOrangeDark
                            )
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        AssistChip(
                            onClick = {},
                            label = { Text(item.errorCode, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = AldawOrangeLight,
                                labelColor = AldawCharcoalDark
                            )
                        )
                    }

                    Text(
                        text = "العَرَض: ${item.symptoms}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    selectedFault?.let { fault ->
        AlertDialog(
            onDismissRequest = { selectedFault = null },
            title = { Text(fault.title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("السبب المباشر المحتمل:", fontWeight = FontWeight.Bold, color = AldawCharcoalDark)
                    Text("• ${fault.primaryCause}\n• ${fault.secondaryCause}", fontSize = 13.sp)

                    Text("إجراءات السلامة والحل الميداني:", fontWeight = FontWeight.Bold, color = AldawSolarGreen)
                    Text(fault.safetyProcedure, fontSize = 13.sp)

                    Text("كود الخطأ القياسي: ${fault.errorCode}", fontSize = 12.sp, color = AldawOrangeDark, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedFault = null
                        onRequestTechnician()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AldawOrange)
                ) {
                    Text("طلب فني معتمد لحل العطل")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedFault = null }) {
                    Text("إغلاق")
                }
            }
        )
    }
}
