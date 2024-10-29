package com.oliver.wallet.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CoinModel::class], version = 1, exportSchema = false)
abstract class CoinDatabase : RoomDatabase() {

    abstract fun coinDao(): CoinDao

    companion object {
        @Volatile
        private var Instance: CoinDatabase? = null

        fun getDatabase(context: Context): CoinDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CoinDatabase::class.java, "coin_database")
                    .addCallback(DatabaseCallback())
                    .build().also { Instance = it }
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insira valores no banco de dados ao criá-lo
                Instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = database.coinDao()
                        dao.insert(CoinModel(label = "Valor1", image = "", favorite = false))
                        dao.insert(CoinModel(label = "Valor1", image = "", favorite = false))
                        dao.insert(CoinModel(label = "Valor1", image = "", favorite = false))
                        dao.insert(CoinModel(label = "Valor1", image = "", favorite = false))
                        dao.insert(CoinModel(label = "Valor1", image = "", favorite = false))
                        dao.insert(CoinModel(label = "Valor1", image = "", favorite = false))
                        dao.insert(CoinModel(label = "Valor1", image = "", favorite = false))
                        dao.insert(CoinModel(label = "Valor1", image = "", favorite = false))
                    }
                }
            }
        }
    }
}