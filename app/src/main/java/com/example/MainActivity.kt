package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.datastore.UserPreferencesRepository
import com.example.data.local.room.WalletTransactionEntity
import com.example.data.repository.AppRepository
import com.example.domain.model.Role
import com.example.domain.model.Technician
import com.example.feature.diagnosis.FaultDiagnosisScreen
import com.example.feature.home.HomeScreen
import com.example.feature.profile.ProfileScreen
import com.example.feature.solar.SolarCalculatorScreen
import com.example.feature.store.StoreScreen
import com.example.feature.tools.TechnicianToolsScreen
import com.example.feature.wallet.WalletScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AlDawApp()
            }
        }
    }
}

enum class NavigationItem(val route: String, val titleAr: String, val icon: ImageVector) {
    HOME("home", "الرئيسية", Icons.Default.Home),
    SOLAR("solar", "حاسبة الطاقة", Icons.Default.WbSunny),
    DIAGNOSIS("diagnosis", "الأعطال", Icons.Default.BuildCircle),
    TOOLS("tools", "أدوات الفني", Icons.Default.Handyman),
    STORE("store", "المتجر", Icons.Default.ShoppingBag),
    WALLET("wallet", "المحفظة", Icons.Default.AccountBalanceWallet),
    PROFILE("profile", "الحساب", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlDawApp() {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val userPrefs = remember { UserPreferencesRepository(context) }
    val scope = rememberCoroutineScope()

    val userRoleStr by userPrefs.userRoleFlow.collectAsState(initial = "CUSTOMER")
    val currentRole = remember(userRoleStr) {
        try {
            Role.valueOf(userRoleStr)
        } catch (e: Exception) {
            Role.CUSTOMER
        }
    }

    val walletBalance by userPrefs.walletBalanceFlow.collectAsState(initial = 150000.0)
    val products by repository.productsFlow.collectAsState(initial = emptyList())
    val transactions by repository.transactionsFlow.collectAsState(initial = emptyList())

    var currentRoute by remember { mutableStateOf("home") }
    var selectedTechForBooking by remember { mutableStateOf<Technician?>(null) }
    var bookingConfirmed by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "شعار الضوء الإلكترونيك",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LightSurfaceVariant)
                        )
                        Column {
                            Text(
                                text = "الضوء الإلكترونيك",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = AldawCharcoalDark
                            )
                            Text(
                                text = "AL-DAW ELECTRONIC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AldawOrangeDark,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                actions = {
                    AssistChip(
                        onClick = { currentRoute = "profile" },
                        label = { Text(currentRole.titleAr, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = AldawOrangeLight,
                            labelColor = AldawCharcoalDark
                        ),
                        modifier = Modifier.padding(end = 8.dp).testTag("role_badge")
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navItems = when (currentRole) {
                    Role.CUSTOMER -> listOf(NavigationItem.HOME, NavigationItem.SOLAR, NavigationItem.DIAGNOSIS, NavigationItem.STORE, NavigationItem.WALLET)
                    Role.TECHNICIAN -> listOf(NavigationItem.HOME, NavigationItem.TOOLS, NavigationItem.SOLAR, NavigationItem.DIAGNOSIS, NavigationItem.WALLET)
                    Role.MERCHANT -> listOf(NavigationItem.HOME, NavigationItem.STORE, NavigationItem.WALLET, NavigationItem.PROFILE)
                    Role.ADMIN -> listOf(NavigationItem.HOME, NavigationItem.STORE, NavigationItem.WALLET, NavigationItem.PROFILE)
                }

                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = { currentRoute = item.route },
                        icon = { Icon(item.icon, contentDescription = item.titleAr) },
                        label = {
                            Text(
                                item.titleAr,
                                fontSize = 11.sp,
                                fontWeight = if (currentRoute == item.route) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AldawOrangeDark,
                            indicatorColor = AldawOrange
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when (currentRoute) {
                "home" -> HomeScreen(
                    role = currentRole,
                    walletBalance = walletBalance,
                    technicians = repository.getTechnicians(),
                    onNavigate = { currentRoute = it },
                    onRequestBooking = { selectedTechForBooking = it }
                )
                "solar" -> SolarCalculatorScreen(
                    onSaveCalculation = { calc ->
                        scope.launch { repository.saveCalculation(calc) }
                    },
                    onRequestInstall = { currentRoute = "home" }
                )
                "diagnosis" -> FaultDiagnosisScreen(
                    faults = repository.getDiagnoses(),
                    onRequestTechnician = { currentRoute = "home" }
                )
                "tools" -> TechnicianToolsScreen()
                "store" -> StoreScreen(
                    products = products,
                    walletBalance = walletBalance,
                    onPurchase = { prod ->
                        scope.launch {
                            val newBal = walletBalance - prod.price
                            userPrefs.setWalletBalance(newBal)
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            repository.recordTransaction(
                                WalletTransactionEntity(
                                    title = "شراء ${prod.name}",
                                    amount = prod.price,
                                    isDeposit = false,
                                    reference = "ORD-${System.currentTimeMillis() % 100000}",
                                    date = dateFormat.format(Date())
                                )
                            )
                        }
                    },
                    onToggleFavorite = { id, isFav ->
                        scope.launch { repository.toggleFavorite(id, isFav) }
                    }
                )
                "wallet" -> WalletScreen(
                    balance = walletBalance,
                    transactions = transactions,
                    onDeposit = { amount, ref ->
                        scope.launch {
                            val newBal = walletBalance + amount
                            userPrefs.setWalletBalance(newBal)
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            repository.recordTransaction(
                                WalletTransactionEntity(
                                    title = "شحن عبر ماي كاشي",
                                    amount = amount,
                                    isDeposit = true,
                                    reference = ref,
                                    date = dateFormat.format(Date())
                                )
                            )
                        }
                    }
                )
                "profile" -> ProfileScreen(
                    currentRole = currentRole,
                    onSelectRole = { newRole ->
                        scope.launch { userPrefs.setUserRole(newRole.name) }
                    }
                )
            }
        }
    }

    selectedTechForBooking?.let { tech ->
        AlertDialog(
            onDismissRequest = { selectedTechForBooking = null },
            icon = { Icon(Icons.Default.Engineering, contentDescription = null, tint = AldawOrange, modifier = Modifier.size(36.dp)) },
            title = { Text("طلب خدمة صيانة / تركيب", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("اسم الفني: ${tech.name}", fontWeight = FontWeight.Bold)
                    Text("التخصص: ${tech.specialty}")
                    Text("الموقع: ${tech.location}")
                    Text("سيتم إرسال إشعار فوري للفني لتحديد موعد الزيارة الميدانية وتأكيد السعر.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedTechForBooking = null
                        bookingConfirmed = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AldawOrange)
                ) {
                    Text("تأكيد إرسال الطلب")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTechForBooking = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (bookingConfirmed) {
        AlertDialog(
            onDismissRequest = { bookingConfirmed = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AldawSolarGreen, modifier = Modifier.size(40.dp)) },
            title = { Text("تم إرسال الطلب بنجاح!") },
            text = { Text("تم تسجيل موعد مبدئي مع الفني، وسيتم التواصل معك هاتفياً أو عبر الرسائل.") },
            confirmButton = {
                Button(onClick = { bookingConfirmed = false }, colors = ButtonDefaults.buttonColors(containerColor = AldawOrange)) {
                    Text("حسناً")
                }
            }
        )
    }
}
