package se.insektionen.songbook.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

fun Context.hideSoftKeyboard(view: View?) {
    if (null != view) {
        val imm: InputMethodManager = this.getSystemService()!!
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
