package com.example.feature.home

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.AlDawHeroCard
import com.example.domain.model.Role
import com.example.domain.model.Technician
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    role: Role,
    walletBalance: Double,
    technicians: List<Technician>,
    onNavigate: (String) -> Unit,
    onRequestBooking: (Technician) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AlDawHeroCard()
        }

        item {
            // Quick Wallet Glance
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate("wallet") }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AldawOrangeLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = "المحفظة",
                                tint = AldawOrangeDark
                            )
                        }
                        Column {
                            Text(
                                "رصيد المحفظة الرقمية",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "%,.0f جنيه سوداني".format(walletBalance),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AldawCharcoalDark
                            )
                        }
                    }
                    FilledTonalButton(
                        onClick = { onNavigate("wallet") },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AldawOrangeLight,
                            contentColor = AldawOrangeDark
                        )
                    ) {
                        Text("شحن ماي كاشي", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "الخدمات والميزات الرئيسية",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = AldawCharcoalDark
            )
        }

        // 2x2 Grid cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    title = "حاسبة الطاقة الشمسية",
                    subtitle = "حساب دقيق للألواح والبطاريات والتكلفة",
                    icon = Icons.Default.WbSunny,
                    iconBg = AldawOrangeLight,
                    iconTint = AldawOrangeDark,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("solar") }
                )
                FeatureCard(
                    title = "تشخيص الأعطال",
                    subtitle = "إرشاد فوري لحل أعطال الكهرباء والإنفرتر",
                    icon = Icons.Default.BuildCircle,
                    iconBg = Color(0xFFE0F2FE),
                    iconTint = AldawAccentBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("diagnosis") }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    title = "أدوات الفني الميدانية",
                    subtitle = "مقطع السلك، هبوط الجهد، دليل الرموز",
                    icon = Icons.Default.Handyman,
                    iconBg = Color(0xFFDCFCE7),
                    iconTint = AldawSolarGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("tools") }
                )
                FeatureCard(
                    title = "متجر المكونات",
                    subtitle = "ألواح، عواكس، بطاريات جيل أصلية",
                    icon = Icons.Default.ShoppingBag,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = AldawOrangeDark,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("store") }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فنيون معتمدون بالقرب منك",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AldawCharcoalDark
                )
                Text(
                    text = "توثيق رسمي",
                    fontSize = 12.sp,
                    color = AldawOrangeDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(technicians) { tech ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AldawCharcoalDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            tech.name.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(tech.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "موثق",
                                tint = AldawOrangeDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            tech.specialty,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(tech.location, fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = AldawOrange,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                "${tech.rating} (${tech.reviewsCount})",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Button(
                        onClick = { onRequestBooking(tech) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AldawOrange)
                    ) {
                        Text("طلب خدمة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconTint)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }
    }
}
