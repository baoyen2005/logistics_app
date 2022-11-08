package com.example.baseapp.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.baseapp.R
import com.vnpay.merchant.ui.views.AbstractTextWatcher
import kotlinx.android.synthetic.main.custom_layout_edittext_round.view.*

class EditTextRound : RelativeLayout {
    var onClearData: (() -> Unit)? = null
    var onBackPress: (() -> Unit)? = null
    var onActionDone: (() -> Unit)? = null
    var onTextChange: ((CharSequence?) -> Unit)? = null
    var onEditClick: (() -> Unit)? = null
    var onFocusChange: ((Boolean) -> Unit)? = null

    val textChangeListener = object : AbstractTextWatcher(){
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (s.toString().isNotEmpty()) {
                if (!isHideClear){
                    imgClear.visibility = View.VISIBLE
                }else {
                    imgClear.visibility = View.GONE
                }
                if (isPassword){
                    imgShowPassword.visibility = View.VISIBLE
                }
                edtRound.setTypeface(valueFont)
            } else {
                edtRound.setTypeface(hintFont)
                imgClear.visibility = View.GONE
                imgShowPassword.visibility = View.GONE
            }
            onTextChange?.invoke(s)
        }

    }

    private var isPassword = false

    private var isHideClear = false

    private var hintFont: Typeface? = null
    private var valueFont: Typeface? = null

    constructor(context: Context?) : super(context) {
        init(context, null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        inflate(context, R.layout.custom_layout_edittext_round, this)
        val density = getContext().resources.displayMetrics.density
        val typedArray: TypedArray =
            getContext().obtainStyledAttributes(attrs, R.styleable.EditTextRound)

        val textValueColor = typedArray.getColor(R.styleable.EditTextRound_valueColor, ContextCompat.getColor(getContext(),R.color.merchant_color_4a4a4a) )
        edtRound.setTextColor(textValueColor)

        val hintString = typedArray.getString(R.styleable.EditTextRound_android_hint) ?: ""
        edtRound.hint = hintString

        val hintStringColor = typedArray.getColor(R.styleable.EditTextRound_hintColor, ContextCompat.getColor(getContext(),R.color.merchant_color_d0d0d2))
        edtRound.setHintTextColor(hintStringColor)

        hintFont = getTypeFace(typedArray.getInt(R.styleable.EditTextRound_hintFont,0))
        valueFont = getTypeFace(typedArray.getInt(R.styleable.EditTextRound_valueFont,1))


        val iconLeftDrawable = typedArray.getDrawable(R.styleable.EditTextRound_srcIconLeft)
        if (iconLeftDrawable != null) {
            imgLeft.visibility = View.VISIBLE
            imgLeft.setImageDrawable(iconLeftDrawable)
        }

        val backgroundDrawable = typedArray.getDrawable(R.styleable.EditTextRound_android_background)
        if (backgroundDrawable != null) {
            llRootView.background = backgroundDrawable
        }

        val imeOption = typedArray.getInteger(R.styleable.EditTextRound_android_imeOptions, EditorInfo.IME_ACTION_DONE)
        edtRound.imeOptions = imeOption

        val inputType = typedArray.getInteger(R.styleable.EditTextRound_android_inputType, InputType.TYPE_CLASS_TEXT)
        edtRound.inputType = inputType

        if (inputType == (InputType.TYPE_NUMBER_VARIATION_PASSWORD + InputType.TYPE_CLASS_NUMBER)
            || inputType == (InputType.TYPE_TEXT_VARIATION_PASSWORD + InputType.TYPE_CLASS_TEXT) ){
            isPassword = true
        }
        edtRound.typeface = hintFont

        edtRound.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (!isHideClear){
                        imgClear.visibility = View.VISIBLE
                    }else {
                        imgClear.visibility = View.GONE
                    }
                    if (isPassword){
                        imgShowPassword.visibility = View.VISIBLE
                    }
                onEditClick?.invoke()
            }else {
                    imgClear.visibility = View.GONE
                    imgShowPassword.visibility = View.GONE
                }
            onFocusChange?.invoke(hasFocus)

        }
        edtRound.addTextChangedListener(textChangeListener)

        edtRound.setOnClickListener {
            edtRound.requestFocus()
            edtRound.showKeyboard()
            onEditClick?.invoke()
        }
        edtRound.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                onActionDone?.invoke()
                edtRound.clearFocus()
                edtRound.hideKeyboard()
                true
            } else {
                false
            }
        }

        imgClear.setOnClickListener {
            edtRound.setText("")
            onClearData?.invoke()
        }

        if (isPassword) {
            imgShowPassword.setOnClickListener {
                if (imgShowPassword.tag == "hide") {
                    edtRound.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    imgShowPassword.setImageResource(R.drawable.ic_show_password)
                    imgShowPassword.tag = "show"
                } else {
                    edtRound.transformationMethod = PasswordTransformationMethod.getInstance()
                    imgShowPassword.setImageResource(R.drawable.ic_icon_hide_password)
                    imgShowPassword.tag = "hide"
                }
            }
        }
        typedArray.recycle()
    }

    fun setDrawableLeft(icon: Drawable?) {
        imgLeft.setImageDrawable(icon)
    }
    fun visibleDrawableLeft(visible: Int) {
        imgLeft.visibility  = visible
    }

    fun setDrawableLeft(icon: Int) {
        imgLeft.setImageResource(icon)
    }

    fun setBackground(resourceId: Int) {
        llRootView.setBackgroundResource(resourceId)
    }

    fun setColorText(resourceId: Int) {
        edtRound.setTextColor(resourceId)
    }

    fun setEditText(message: String) {
        edtRound.removeTextChangedListener(textChangeListener)
        edtRound.setText(message)
        edtRound.addTextChangedListener(textChangeListener)
       if (TextUtils.isEmpty(message)) {
           edtRound.setTypeface(hintFont)
       } else {
           edtRound.setTypeface(valueFont)
       }

    }

    fun setHideClearBtn() {
        isHideClear = true
    }


    fun getTextEdit(): String{
        return edtRound.text.toString()
    }



    private fun getTypeFace(value: Int) : Typeface? {
            var typeface: Typeface? = null
            when (value) {
                0 -> {
                    typeface = ResourcesCompat.getFont(context, R.font.ssp_regular)

                }
                1 -> {
                    typeface = ResourcesCompat.getFont(context, R.font.ssp_bold)

                }
                2 -> {
                    typeface = ResourcesCompat.getFont(context, R.font.sf_medium)
                }
                3 -> {
                    typeface = ResourcesCompat.getFont(context, R.font.sf_lightitalic)

                }
                4 -> {
                    typeface = ResourcesCompat.getFont(context, R.font.ssp_semibold)

                }
            }
            return  typeface
        }


}