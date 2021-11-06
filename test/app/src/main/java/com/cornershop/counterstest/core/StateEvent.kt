package com.cornershop.counterstest.core

interface StateEvent {
    fun errorInfo(): String
    fun eventName(): String
    fun shouldDisplayProgressBar(): Boolean
}