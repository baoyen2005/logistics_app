package com.example.baseapp

open class BaseVHData<T>(data: T) {
    var type = 0
    var realData: T? = data
}