package com.example.dawaidata.common.utility

import android.content.Context

fun showAlert(context: Context, title: String, message: String) {
    val builder = androidx.appcompat.app.AlertDialog.Builder(context)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton("OK", null)
    val dialog = builder.create()
    dialog.show()
}
