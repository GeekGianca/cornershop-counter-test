package com.cornershop.counterstest.core

data class ErrorState(
    var message: String,
    var code:Int = 0,
)

interface ErrorStateCallback {
    fun removeErrorFromStack()
}