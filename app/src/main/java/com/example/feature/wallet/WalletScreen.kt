package com.example.feature.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.room.WalletTransactionEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    balance: Double,
    transactions: List<WalletTransactionEntity>,
    onDeposit: (Double, String) -> Unit
) {
    var showDepositDialog by remember { mutableStateOf(false) }
    var depositAmountText by remember { mutableStateOf("50000") }
    var referenceText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AldawCharcoalDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("الرصيد المتاح في المحفظة الإلكترونية", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                    Text(
                        "%,.0f SDG".format(balance),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AldawOrange
                    )
                    Text("مدفوعات آمنة لخدمات الصيانة ومشتريات الألواح والانفرتر", fontSize = 11.sp, color = Color(0xFF94A3B8))

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { showDepositDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AldawOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddCard, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("شحن المحفظة عبر ماي كاشي (MyCashi)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "سجل المعاملات والعمليات",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = AldawCharcoalDark
            )
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("لا توجد عمليات سابقة", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(transactions) { tx ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tx.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${tx.date} • رقم المرجع: ${tx.reference}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Text(
                            text = "${if (tx.isDeposit) "+" else "-"} %,.0f جنيه".format(tx.amount),
                            color = if (tx.isDeposit) AldawSolarGreen else Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    if (showDepositDialog) {
        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = { Text("شحن المحفظة عبر ماي كاشي", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("أدخل المبلغ ورقم عملية الإيداع في ماي كاشي:")
                    OutlinedTextField(
                        value = depositAmountText,
                        onValueChange = { depositAmountText = it },
                        label = { Text("المبلغ (جنيه سوداني)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = referenceText,
                        onValueChange = { referenceText = it },
                        label = { Text("رقم مرجع الإشعار (اختياري)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = depositAmountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            val ref = referenceText.ifEmpty { "CASHI-${System.currentTimeMillis() % 100000}" }
                            onDeposit(amt, ref)
                            showDepositDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AldawOrange)
                ) {
                    Text("تأكيد الشحن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
