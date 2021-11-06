package com.cornershop.counterstest.core

import com.cornershop.counterstest.data.models.TitleCounterModel

interface MainInteraction {
    fun onCreateCounter(model: TitleCounterModel)
    fun onErrorReceived(errorState: ErrorState, errorStateCallback: ErrorStateCallback)
}