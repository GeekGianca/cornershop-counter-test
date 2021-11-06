package com.cornershop.counterstest.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ErrorStack : ArrayList<ErrorState>() {
    private val _errorState: MutableLiveData<ErrorState?> = MutableLiveData()

    val errorState: LiveData<ErrorState?>
        get() = _errorState

    fun isStackEmpty(): Boolean = size == 0

    override fun addAll(elements: Collection<ErrorState>): Boolean {
        for (element in elements)
            add(element)
        return true
    }

    override fun add(element: ErrorState): Boolean {
        if (this.contains(element))
            return false

        if (this.size == 0) {
            super.add(element)
            setErrorState(errorState = element)
            return true
        }
        super.add(element)
        return true
    }

    override fun removeAt(index: Int): ErrorState {
        if (this.size > 0) {
            try {
                val transaction = super.removeAt(index)
                if (this.size > 0)
                    setErrorState(errorState = this[0])
                else
                    setErrorState(null)
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        } else {
            setErrorState(null)
        }
        return ErrorState("Something else")
    }

    private fun setErrorState(errorState: ErrorState?) {
        _errorState.postValue(errorState)
    }
}