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