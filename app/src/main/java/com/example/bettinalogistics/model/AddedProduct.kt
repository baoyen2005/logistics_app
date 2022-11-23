package com.example.bettinalogistics.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AddedProduct")
data class AddedProduct(
    var productList: String?
){
    @PrimaryKey(autoGenerate = true) var id = 0
}