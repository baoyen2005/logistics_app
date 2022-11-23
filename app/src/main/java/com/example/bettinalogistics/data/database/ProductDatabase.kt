package com.example.bettinalogistics.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bettinalogistics.model.AddedProduct
import com.example.bettinalogistics.model.Product

@Database(entities = [AddedProduct::class, Product::class], version = 1, exportSchema = false)
abstract class ProductDatabase : RoomDatabase(){
    abstract fun productOrderDao() : ProductOrderDao
    companion object {
        // Singleton prevents multiple
        // instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ProductDatabase? = null

        fun getDatabase(context: Context): ProductDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "product_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}