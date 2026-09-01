package com.example.feature.store

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.Product
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    products: List<Product>,
    walletBalance: Double,
    onPurchase: (Product) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProductForBuy by remember { mutableStateOf<Product?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val categories = listOf("الكل", "ألواح شمسية", "محولات وانفرترات", "بطاريات وتخزين", "أسلاك وكوابل", "قواطع وحماية")

    val filteredProducts = products.filter { p ->
        val catMatches = (selectedCategory == "الكل" || p.category == selectedCategory)
        val searchMatches = searchQuery.isEmpty() ||
                p.name.contains(searchQuery, ignoreCase = true) ||
                p.specs.contains(searchQuery, ignoreCase = true)
        catMatches && searchMatches
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "متجر الضوء الإلكترونيك",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AldawCharcoalDark
            )
            Text(
                text = "معدات طاقة شمسية وكهرباء أصلية معتمدة مع ضمان معتمد",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في المنتجات والمعدات...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 0.dp,
                divider = {}
            ) {
                categories.forEach { cat ->
                    Tab(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        text = { Text(cat, fontSize = 12.sp, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }

        items(filteredProducts) { product ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(product.merchantName, fontSize = 11.sp, color = AldawOrangeDark)
                        }
                        IconButton(onClick = { onToggleFavorite(product.id, !product.isFavorite) }) {
                            Icon(
                                if (product.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "المفضلة",
                                tint = if (product.isFavorite) Color.Red else Color.Gray
                            )
                        }
                    }

                    Text(product.specs, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    HorizontalDivider(color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("السعر المعتمد", fontSize = 10.sp, color = Color.Gray)
                            Text(
                                "%,.0f SDG".format(product.price),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = AldawCharcoalDark
                            )
                        }

                        Button(
                            onClick = { selectedProductForBuy = product },
                            colors = ButtonDefaults.buttonColors(containerColor = AldawOrange),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("شراء بالمحفظة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    selectedProductForBuy?.let { prod ->
        val canAfford = walletBalance >= prod.price
        AlertDialog(
            onDismissRequest = { selectedProductForBuy = null },
            title = { Text("تأكيد طلب الشراء", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(prod.name, fontWeight = FontWeight.SemiBold)
                    Text("المبلغ الإجمالي: %,.0f جنيه سوداني".format(prod.price))
                    Text("رصيد محفظتك: %,.0f جنيه سوداني".format(walletBalance))

                    if (!canAfford) {
                        Text(
                            "تنبيه: الرصيد غير كافٍ، يرجى شحن محفظتك عبر ماي كاشي أولاً.",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (canAfford) {
                            onPurchase(prod)
                            selectedProductForBuy = null
                            showSuccessDialog = true
                        }
                    },
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = AldawOrange)
                ) {
                    Text("خصم وتأكيد الطلب")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProductForBuy = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AldawSolarGreen, modifier = Modifier.size(40.dp)) },
            title = { Text("تمت عملية الشراء بنجاح!") },
            text = { Text("تم خصم المبلغ من محفظتك الإلكترونية وتوليد إشعار فوري للتاجر لتسليم الطلب.") },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = AldawOrange)) {
                    Text("حسناً")
                }
            }
        )
    }
}
