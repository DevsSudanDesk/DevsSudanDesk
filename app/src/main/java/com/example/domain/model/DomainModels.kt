package com.example.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class Role(val titleAr: String, val subtitle: String) {
    CUSTOMER("عميل", "طلب خدمات وحساب الطاقة والتسوق"),
    TECHNICIAN("فني", "أدوات هندسية واستقبال المهام"),
    MERCHANT("تاجر", "إدارة المنتجات والمبيعات"),
    ADMIN("الإدارة", "توثيق ومراقبة المنظومة")
}

data class Technician(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double,
    val reviewsCount: Int,
    val location: String,
    val phone: String,
    val isVerified: Boolean = true
)

data class SolarSystemPlan(
    val systemKw: Double,
    val panelsCount: Int,
    val panelWatts: Int = 550,
    val batteryCapacityAh: Int,
    val inverterSizeKw: Double,
    val estimatedCostSdg: Double,
    val dailyGenerationKwh: Double
)

data class FaultItem(
    val id: String,
    val category: String,
    val title: String,
    val symptoms: String,
    val primaryCause: String,
    val secondaryCause: String,
    val safetyProcedure: String,
    val errorCode: String,
    val isCritical: Boolean = false
)

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val specs: String,
    val price: Double,
    val stock: Int,
    val merchantName: String,
    val isFavorite: Boolean = false
)

data class MaintenanceTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val note: String = ""
)
