package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.room.AppDatabase
import com.example.data.local.room.ProductEntity
import com.example.data.local.room.SolarCalculationEntity
import com.example.data.local.room.WalletTransactionEntity
import com.example.domain.model.FaultItem
import com.example.domain.model.Product
import com.example.domain.model.Technician
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AppRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "aldaw_electronic.db"
    ).fallbackToDestructiveMigration().build()

    private val solarDao = db.solarCalculationDao()
    private val walletDao = db.walletTransactionDao()
    private val productDao = db.productDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            productDao.insertAll(
                listOf(
                    ProductEntity(
                        id = "p1",
                        name = "لوح شمسي Longi 550W Tier 1",
                        category = "ألواح شمسية",
                        specs = "مونوكريستال نصف خلية عالي الكفاءة مع ضمان 25 سنة",
                        price = 85000.0,
                        stock = 45,
                        merchantName = "شركة النيل للطاقة الشمسية"
                    ),
                    ProductEntity(
                        id = "p2",
                        name = "انفرتر هجين Deye 5.5kW Hybrid",
                        category = "محولات وانفرترات",
                        specs = "نظام ذكي 48V مع شاحن MPPT متكامل وشاشة لمس",
                        price = 490000.0,
                        stock = 12,
                        merchantName = "وكالة الخرطوم للكهرباء"
                    ),
                    ProductEntity(
                        id = "p3",
                        name = "بطارية جيل Narada 200Ah 12V",
                        category = "بطاريات وتخزين",
                        specs = "دورة تفريغ عميق ممتازة للمناخ الحار وتخزين طويل الأمد",
                        price = 175000.0,
                        stock = 30,
                        merchantName = "مؤسسة الطاقة البديلة"
                    ),
                    ProductEntity(
                        id = "p4",
                        name = "كابل طاقة شمسية 6mm² DC Solar Cable",
                        category = "أسلاك وكوابل",
                        specs = "نحاس نقي معزول مزدوج مقاوم للأشعة فوق البنفسجية 100م",
                        price = 68000.0,
                        stock = 100,
                        merchantName = "المركز الفني للهندسة"
                    ),
                    ProductEntity(
                        id = "p5",
                        name = "قاطع حماية أوتوماتيكي DC Breaker 63A",
                        category = "قواطع وحماية",
                        specs = "قاطع تيار مستمر للألواح والبطاريات 500V 2P",
                        price = 18500.0,
                        stock = 60,
                        merchantName = "موزعي الكهرباء المعتمدين"
                    )
                )
            )
        }
    }

    val productsFlow: Flow<List<Product>> = productDao.getAllProducts().map { entities ->
        entities.map {
            Product(
                id = it.id,
                name = it.name,
                category = it.category,
                specs = it.specs,
                price = it.price,
                stock = it.stock,
                merchantName = it.merchantName,
                isFavorite = it.isFavorite
            )
        }
    }

    val calculationsFlow = solarDao.getAllCalculations()
    val transactionsFlow = walletDao.getAllTransactions()

    suspend fun saveCalculation(calc: SolarCalculationEntity) = solarDao.insertCalculation(calc)

    suspend fun recordTransaction(tx: WalletTransactionEntity) = walletDao.insertTransaction(tx)

    suspend fun toggleFavorite(productId: String, isFav: Boolean) = productDao.updateFavorite(productId, isFav)

    fun getTechnicians(): List<Technician> = listOf(
        Technician("t1", "م. عثمان إبراهيم النور", "مهندس طاقة شمسية وانفرترات", 4.9, 84, "الخرطوم - بحري", "+249912345678", true),
        Technician("t2", "الفني طارق عبد الله أحمد", "فني كهرباء منازل وتمديدات", 4.8, 62, "أم درمان - الثورة", "+249923456789", true),
        Technician("t3", "م. حسام الدين صديق", "تركيب أنظمة طاقة ومضخات شمسية", 4.9, 110, "الخرطوم - الرياض", "+249934567890", true),
        Technician("t4", "الفني محمد الفاتح", "صيانة قواطع ومولدات ولوحات تحكم", 4.7, 49, "الجزيرة - ود مدني", "+249945678901", true)
    )

    fun getDiagnoses(): List<FaultItem> = listOf(
        FaultItem(
            id = "d1",
            category = "طاقة شمسية",
            title = "انقطاع إنتاج الألواح بالرغم من سطوع الشمس",
            symptoms = "شاشة الانفرتر تظهر PV 0V أو صفر واط في ساعات الظهيرة",
            primaryCause = "فصل في قاطع التيار المستمر (DC Breaker) أو ذوبان في وصلات MC4",
            secondaryCause = "تلف أو احتراق ديود الحماية في صندوق تجميع الألواح",
            safetyProcedure = "افصل قاطع DC وافحص الجهد بواسطة الملتميتر قبل لمس الكابلات المعراة.",
            errorCode = "PV Loss / Error 02",
            isCritical = false
        ),
        FaultItem(
            id = "d2",
            category = "طاقة شمسية",
            title = "صافرة إنذار مستمرة من الانفرتر وتوقف التغذية",
            symptoms = "الانفرتر يصدر نغمة تحذيرية مع وميض الليد الأحمر",
            primaryCause = "انخفاض جهد البطارية تحت الحد الأدنى للأمان (Low Battery Cutoff)",
            secondaryCause = "تحميل زائد (Overload) يفوق قدرة العاكس عند إقلاع موتور أو مكيف",
            safetyProcedure = "افصل جميع الأحمال الثقيلة واترك المنظومة تشحن البطاريات لمدة ساعة.",
            errorCode = "Low Battery / Overload (F04/F07)",
            isCritical = true
        ),
        FaultItem(
            id = "d3",
            category = "كهرباء منزلية",
            title = "القاطع التفاضلي (Earth Leakage) يفصل فجأة",
            symptoms = "انقطاع الكهرباء عن جزء أو كل المنزل عند تشغيل جهاز معين",
            primaryCause = "تسريب تيار أرضي ناتج عن رطوبة في مأخذ كهربائي أو عطب في سخان/غسالة",
            secondaryCause = "تلف في العازل الداخلي لأحد الأسلاك المغذية",
            safetyProcedure = "افصل جميع القواطع الفرعية ثم ارفع القاطع الرئيسي وأعد القواطع واحداً تلو الآخر لتحديد الدائرة المعطوبة.",
            errorCode = "Earth Leakage Trip",
            isCritical = true
        ),
        FaultItem(
            id = "d4",
            category = "بطاريات وتخزين",
            title = "نفاد شحن البطارية بسرعة غير معتادة ليلاً",
            symptoms = "المنظومة تفصل بعد ساعة واحدة من غياب الشمس",
            primaryCause = "وصول خلايا البطارية لنهاية عمرها الافتراضي (Sulfation / جفاف الإلكترولايت)",
            secondaryCause = "عدم اكتمال الشحن نهاراً بسبب زاوية توجيه الألواح أو الغبار",
            safetyProcedure = "افحص جهد كل بطارية على حدة تحت الحمل لتحديد الخلية التالفة واستبدالها.",
            errorCode = "Battery Capacity Degraded",
            isCritical = false
        )
    )
}
