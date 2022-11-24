package com.example.bettinalogistics.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Delete
import com.example.bettinalogistics.data.database.ProductOrderDao
import com.example.bettinalogistics.model.AddedProduct
import com.example.bettinalogistics.model.Product

class AddedProductToDbRepo(private val productOrderDao: ProductOrderDao) {
    suspend fun insertProduct(product: Product) = productOrderDao.insertProduct(product)

    suspend fun insertAddedProduct(addedProduct: AddedProduct) = productOrderDao.insertAddedProduct(addedProduct)

    suspend fun deleteAllAddedProduct() = productOrderDao.deleteAllAddedProduct()

    suspend fun deleteAllProduct() = productOrderDao.deleteAllProduct()

    suspend fun deleteAddedProduct(addedProduct: AddedProduct) = productOrderDao.deleteAddedProduct(addedProduct)

    suspend fun deleteProduct(product: Product) = productOrderDao.deleteProduct(product)

    fun getAllAddedProduct(): LiveData<List<AddedProduct>> = productOrderDao.getAllAddedProduct()

    fun getAllProduct(): LiveData<List<Product>> = productOrderDao.getAllProduct()

    suspend fun updateProduct(product: Product) = productOrderDao.updateProduct(product)

    suspend fun updateAddedProduct(addedProduct: AddedProduct) = productOrderDao.updateAddedProduct(addedProduct )
}