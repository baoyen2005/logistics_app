package com.example.bettinalogistics.data

import androidx.lifecycle.MutableLiveData
import androidx.room.Delete
import com.example.bettinalogistics.data.database.ProductOrderDao
import com.example.bettinalogistics.model.AddedProduct
import com.example.bettinalogistics.model.Product

class AddedProductToDbRepo(private val productOrderDao: ProductOrderDao) {
    suspend fun insertProduct(product: Product) = productOrderDao.insertProduct(product)

    suspend fun insertAddedProduct(addedProduct: String) = productOrderDao.insertAddedProduct(addedProduct)

    suspend fun deleteAllAddedProduct() = productOrderDao.deleteAllAddedProduct()

    suspend fun deleteAllProduct() = productOrderDao.deleteAllProduct()

    suspend fun deleteAddedProduct(addedProduct: String) = productOrderDao.deleteAddedProduct(addedProduct)

    suspend fun deleteProduct(product: Product) = productOrderDao.deleteProduct(product)

    fun getAllAddedProduct(): MutableLiveData<List<AddedProduct>> = productOrderDao.getAllAddedProduct()

    fun getAllProduct(): MutableLiveData<List<Product>> = productOrderDao.getAllProduct()

    suspend fun updateProduct(product: Product) = productOrderDao.updateProduct(product)

    suspend fun updateAddedProduct(addedProduct: String) = productOrderDao.updateAddedProduct(addedProduct )
}