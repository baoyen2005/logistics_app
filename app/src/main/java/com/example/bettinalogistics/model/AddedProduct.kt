package com.example.bettinalogistics.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class AddedProduct(
    var productList: List<Product>?
)