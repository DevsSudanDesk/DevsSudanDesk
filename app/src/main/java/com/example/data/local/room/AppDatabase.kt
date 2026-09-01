package com.example.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "solar_calculations")
data class SolarCalculationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val buildingType: String,
    val areaSqm: Double,
    val monthlyConsumptionKwh: Double,
    val peakSunHours: Double,
    val systemSizeKw: Double,
    val panelsCount: Int,
    val batteryCapacityAh: Int,
    val inverterSizeKw: Double,
    val estimatedCost: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val isDeposit: Boolean,
    val reference: String,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val specs: String,
    val price: Double,
    val stock: Int,
    val merchantName: String,
    val isFavorite: Boolean = false
)

@Dao
interface SolarCalculationDao {
    @Query("SELECT * FROM solar_calculations ORDER BY createdAt DESC")
    fun getAllCalculations(): Flow<List<SolarCalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calc: SolarCalculationEntity): Long
}

@Dao
interface WalletTransactionDao {
    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: WalletTransactionEntity)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("UPDATE products SET isFavorite = :isFav WHERE id = :productId")
    suspend fun updateFavorite(productId: String, isFav: Boolean)
}

@Database(
    entities = [SolarCalculationEntity::class, WalletTransactionEntity::class, ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun solarCalculationDao(): SolarCalculationDao
    abstract fun walletTransactionDao(): WalletTransactionDao
    abstract fun productDao(): ProductDao
}
