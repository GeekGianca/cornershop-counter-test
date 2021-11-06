package com.cornershop.counterstest.core

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceManager {
    private const val APP_SETTINGS_KEY = "SETTING_APP"
    private const val APP_STATE_GETTING_STARTED = "appStateStart"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_SETTINGS_KEY, Context.MODE_PRIVATE)
    }

    fun startState(ctx: Context): Boolean =
        getSharedPreferences(ctx).getBoolean(APP_STATE_GETTING_STARTED, false)

    fun startState(ctx: Context, value: Boolean) {
        val editor = getSharedPreferences(ctx).edit()
        editor.putBoolean(APP_STATE_GETTING_STARTED, value)
        editor.apply()
    }
}