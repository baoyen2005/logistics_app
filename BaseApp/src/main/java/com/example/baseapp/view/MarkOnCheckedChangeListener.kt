package com.example.baseapp.view

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import com.example.baseapp.R

class MarkOnCheckedChangeListener(private val edt: EditText, private val ivEye: CheckBox) : CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (!isChecked) {
            edt.transformationMethod = HideReturnsTransformationMethod()
            edt.setSelection(edt.text!!.length)
            ivEye.setButtonDrawable(R.drawable.ic_icon_hide_password)
        } else {
            edt.transformationMethod = PasswordTransformationMethod()
            edt.setSelection(edt.text!!.length)
            ivEye.setButtonDrawable(R.drawable.ic_show_password)
        }
    }
}