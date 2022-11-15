package com.example.baseapp

interface IOnBind<T> {
    fun onBind(data: T)
    fun onBind(data: T, payload: List<Any>)
}