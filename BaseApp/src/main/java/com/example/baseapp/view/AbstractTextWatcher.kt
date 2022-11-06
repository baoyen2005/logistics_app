package com.vnpay.merchant.ui.views

import android.text.Editable
import android.text.TextWatcher

abstract class AbstractTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}
}