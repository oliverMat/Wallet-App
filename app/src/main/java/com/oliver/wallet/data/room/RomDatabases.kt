package com.oliver.wallet.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oliver.wallet.R
import com.oliver.wallet.data.room.dao.CoinDao
import com.oliver.wallet.data.room.dao.RatesDao
import com.oliver.wallet.data.room.model.CoinModel
import com.oliver.wallet.data.room.model.RatesModel
import com.oliver.wallet.util.TypeMoney
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CoinModel::class, RatesModel::class], version = 1, exportSchema = false)
abstract class RomDatabases : RoomDatabase() {

    abstract fun coinDao(): CoinDao

    abstract fun ratesDao(): RatesDao

    companion object {
        @Volatile
        private var Instance: RomDatabases? = null

        fun getDatabase(context: Context): RomDatabases {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, RomDatabases::class.java, "coin_database")
                    .addCallback(DatabaseCallback(context))
                    .build().also { Instance = it }
            }
        }

        private class DatabaseCallback(private val context: Context) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // valores no banco de dados ao criá-lo
                Instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        createCoinModel(database.coinDao())
                        createTaxModel(database.ratesDao())
                    }
                }
            }

            private suspend fun createCoinModel(database: CoinDao) {
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.dollar_name),
                        image = R.drawable.united_stats_flag,
                        typeMoney = TypeMoney.Dollar,
                        isFavorite = true
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.euro_name),
                        image = R.drawable.europe_flag,
                        typeMoney = TypeMoney.Euro,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.brazilianReal_name),
                        image = R.drawable.brasil_flag,
                        typeMoney = TypeMoney.BrazilianReal,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.japaneseYen_name),
                        image = R.drawable.japan_flag,
                        typeMoney = TypeMoney.JapaneseYen,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.poundSterling_name),
                        image = R.drawable.united_kingdom_flag,
                        typeMoney = TypeMoney.PoundSterling,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.australianDollar_name),
                        image = R.drawable.australian_flag,
                        typeMoney = TypeMoney.AustralianDollar,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.canadianDollar_name),
                        image = R.drawable.canadian_flag,
                        typeMoney = TypeMoney.CanadianDollar,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.swissFranc_name),
                        image = R.drawable.switzerland_flag,
                        typeMoney = TypeMoney.SwissFranc,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.chineseYuan_name),
                        image = R.drawable.china_flag,
                        typeMoney = TypeMoney.ChineseYuan,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.swedishKrona_name),
                        image = R.drawable.sweden_flag,
                        typeMoney = TypeMoney.SwedishKrona,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.newZealandDollar_name),
                        image = R.drawable.newzealand_flag,
                        typeMoney = TypeMoney.NewZealandDollar,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.mexicanPeso_name),
                        image = R.drawable.mexico_flag,
                        typeMoney = TypeMoney.MexicanPeso,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.singaporeDollar_name),
                        image = R.drawable.singapore_flag,
                        typeMoney = TypeMoney.SingaporeDollar,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.hongKongDollar_name),
                        image = R.drawable.hongkong_flag,
                        typeMoney = TypeMoney.HongKongDollar,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.norwegianKrone_name),
                        image = R.drawable.norway_flag,
                        typeMoney = TypeMoney.NorwegianKrone,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.turkishLira_name),
                        image = R.drawable.turkey_flag,
                        typeMoney = TypeMoney.TurkishLira,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.indianRupee_name),
                        image = R.drawable.india_flag,
                        typeMoney = TypeMoney.IndianRupee,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.russianRuble_name),
                        image = R.drawable.russia_flag,
                        typeMoney = TypeMoney.RussianRuble,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.southAfricanRand_name),
                        image = R.drawable.southafrica_flag,
                        typeMoney = TypeMoney.SouthAfricanRand,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.chileanPeso_name),
                        image = R.drawable.chile_flag,
                        typeMoney = TypeMoney.ChileanPeso,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.argentinePeso_name),
                        image = R.drawable.argentina_flag,
                        typeMoney = TypeMoney.ArgentinePeso,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.taiwanDollar_name),
                        image = R.drawable.taiwan_flag,
                        typeMoney = TypeMoney.TaiwanDollar,
                        isFavorite = false
                    )
                )
                database.insert(
                    CoinModel(
                        label = context.getString(R.string.thaiBaht_name),
                        image = R.drawable.thailand_flag,
                        typeMoney = TypeMoney.ThaiBaht,
                        isFavorite = false
                    )
                )
            }

            private suspend fun createTaxModel(ratesDao: RatesDao) {
                ratesDao.insert(
                    RatesModel(
                        iof = 0.0038f,
                        spread = 0.025f
                    )
                )
            }
        }
    }
}