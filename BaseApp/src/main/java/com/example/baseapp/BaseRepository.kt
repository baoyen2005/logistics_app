package com.example.baseapp

import kotlinx.coroutines.flow.MutableStateFlow

open class BaseRepository {
    var isLoading = MutableStateFlow(Boolean)
    var message = MutableStateFlow(String)
}