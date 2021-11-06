package com.cornershop.counterstest.core

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.cornershop.counterstest.BuildConfig
import com.cornershop.counterstest.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.createAlert() {
    MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
        .setTitle(this.getString(R.string.error_deleting_counter_title))
        .setMessage(getString(R.string.connection_error_description))
        .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }.show()
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.dismiss() {
    this.visibility = View.INVISIBLE
}

@Throws(Exception::class)
fun Activity.onShareCounter(info: String) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, BuildConfig.APPLICATION_ID)
    shareIntent.putExtra(Intent.EXTRA_TEXT, info)
    startActivity(Intent.createChooser(shareIntent, "Share"))
}