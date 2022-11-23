package com.example.bettinalogistics.data.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.bettinalogistics.model.AddedProduct
import com.example.bettinalogistics.model.Product

@Dao
interface ProductOrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAddedProduct(addedProduct: String)

    @Query("DELETE FROM AddedProduct")
    suspend fun deleteAllAddedProduct()

    @Query("DELETE FROM Product")
    suspend fun deleteAllProduct()

    @Delete
    suspend fun deleteAddedProduct(addedProduct: String)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("Select * from AddedProduct order by id ASC")
    fun getAllAddedProduct(): MutableLiveData<List<AddedProduct>>

    @Query("Select * from Product order by id ASC")
    fun getAllProduct(): MutableLiveData<List<Product>>

    @Update
    suspend fun updateProduct(product: Product)

    @Update
    suspend fun updateAddedProduct(addedProduct: String)
}