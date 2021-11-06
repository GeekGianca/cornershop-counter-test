package com.cornershop.counterstest.core

import android.content.Context
import android.content.DialogInterface
import com.cornershop.counterstest.R
import com.cornershop.counterstest.data.local.entities.CounterEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object Util {

    const val UNKNOWN_ERROR = "Unknown error"

    const val NETWORK_ERROR = "Network error"

    const val NETWORK_TIMEOUT = 3000L // ms (request will timeout)

    const val NETWORK_ERROR_TIMEOUT = "Network timeout"

    const val NETWORK_ERROR_OFFLINE = "No address associated with hostname"

    const val CACHE_TIMEOUT = 3000L // ms (request will timeout)

    const val CACHE_ERROR_TIMEOUT = "Cache timeout"

    const val REFRESH_CACHE_TIME = 1200L // 20 mins

    const val INVALID_STATE_EVENT = "Invalid state event"

    const val UNABLE_TO_RETRIEVE_COUNTERS =
        "Unable to retrieve counters list, but showing local results"

    const val UNABLE_TO_EDIT_COUNTER = "Couldn't update"

    const val UNABLE_TO_SEARCH_COUNTER = "No results"

    const val EMPTY_LIST = "No results in local"
}

fun List<CounterEntity>.toDetail(): Pair<Int, Int> =
    Pair(this.size, this.sumOf { it.count })

fun List<CounterEntity>.toShare(): String =
    this.joinToString(",\n") { String.format("%s x %s", it.count, it.title) }

fun List<CounterEntity>.toDelete(): String =
    this.joinToString(",\n") { String.format("%s", it.title) }

fun Context.displayAlert(
    title: String? = null,
    message: String? = null,
    titlePos: String? = null,
    pos: DialogInterface.OnClickListener? = null,
    titleNeg: String? = null,
    neg: DialogInterface.OnClickListener? = null
) {
    val dialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
    title?.let {
        dialog.setTitle(it)
    }
    message?.let {
        dialog.setMessage(it)
    }
    titlePos?.let {
        dialog.setPositiveButton(it, pos)
    }
    titleNeg?.let {
        dialog.setNegativeButton(it, neg)
    }
    dialog.setCancelable(false)
    dialog.create()
    dialog.show()
}