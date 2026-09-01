package com.example.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.Role
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    currentRole: Role,
    onSelectRole: (Role) -> Unit
) {
    var showRolePicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(AldawOrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = AldawOrangeDark, modifier = Modifier.size(36.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("مستخدم منصة الضوء", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("+249 91 234 5678", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        AssistChip(
                            onClick = { showRolePicker = true },
                            label = { Text("نوع الحساب: ${currentRole.titleAr}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = AldawOrangeLight,
                                labelColor = AldawCharcoalDark
                            )
                        )
                    }
                }
            }
        }

        item {
            Text("إعدادات الحساب والأدوار", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AldawCharcoalDark)
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    ProfileOptionRow(
                        title = "تبديل نوع الحساب / الدور",
                        subtitle = "عميل، فني، تاجر، إدارة",
                        icon = Icons.Default.SwapHoriz,
                        onClick = { showRolePicker = true }
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileOptionRow(
                        title = "توثيق الحساب المهني (KYC)",
                        subtitle = "توثيق الهوية والرخصة المهنية للفنيين والتجار",
                        icon = Icons.Default.VerifiedUser,
                        onClick = {}
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileOptionRow(
                        title = "خدمة العملاء والدعم الفني",
                        subtitle = "تواصل مباشر مع مهندسي منصة الضوء الإلكترونيك",
                        icon = Icons.Default.HeadsetMic,
                        onClick = {}
                    )
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    ProfileOptionRow(
                        title = "حول المنصة",
                        subtitle = "الإصدار 1.0.0 - Al-Daw Electronic",
                        icon = Icons.Default.Info,
                        onClick = {}
                    )
                }
            }
        }
    }

    if (showRolePicker) {
        AlertDialog(
            onDismissRequest = { showRolePicker = false },
            title = { Text("اختر نوع الحساب / الدور", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Role.values().forEach { role ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (currentRole == role) AldawOrangeLight else MaterialTheme.colorScheme.surface
                            ),
                            border = if (currentRole == role) ButtonDefaults.outlinedButtonBorder else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectRole(role)
                                    showRolePicker = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(role.titleAr, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(role.subtitle, fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRolePicker = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun ProfileOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = AldawOrangeDark)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.Gray)
    }
}
