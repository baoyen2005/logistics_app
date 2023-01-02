package com.example.bettinalogistics.ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.baseapp.view.hideKeyboard
import com.example.baseapp.view.setSafeOnClickListener
import com.example.baseapp.view.showKeyboard
import com.example.bettinalogistics.R
import com.example.bettinalogistics.databinding.CustomLayoutEdittextRoundBinding
import com.vnpay.merchant.ui.views.AbstractTextWatcher

class EditTextRound : RelativeLayout {
    var onClearData: (() -> Unit)? = null
    var onBackPress: (() -> Unit)? = null
    var onActionDone: (() -> Unit)? = null
    var onActionDelete: (() -> Unit)? = null
    var onTextChange: ((CharSequence?) -> Unit)? = null
    var onEditClick: (() -> Unit)? = null
    var onFocusChange: ((Boolean) -> Unit)? = null
    var viewText = ""
    var onCancelButtonClick: (() -> Unit)? = null
    var isShowCancelBtn: Boolean = false

    private lateinit var binding: CustomLayoutEdittextRoundBinding

    private val textChangeListener = object : AbstractTextWatcher(){
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            viewText = s.toString()
            if (s.toString().isNotEmpty()) {
                if (!isHideClear){
                    binding.imgClear.visibility = View.VISIBLE
                }else {
                    binding.imgClear.visibility = View.GONE
                }
                if (isPassword){
                    binding.imgShowPassword.visibility = View.VISIBLE
                }
                binding.edtRound.typeface = valueFont
            } else {
                binding.edtRound.typeface = hintFont
                binding.imgClear.visibility = View.GONE
                binding.imgShowPassword.visibility = View.GONE
            }
            if (isShowCancelBtn) {
                binding.tvCancel.isVisible = s.toString().isEmpty()
            }
            onTextChange?.invoke(s)
        }

    }

    private var isPassword = false

    private var isHideClear = false

    private var hintFont: Typeface? = null
    private var valueFont: Typeface? = null

    init {
        compound()
    }

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

    private fun compound() {
        binding = CustomLayoutEdittextRoundBinding.inflate(LayoutInflater.from(context), this,true)
    }

    override fun onDetachedFromWindow() {
        onClearData = null
        onFocusChange = null
        onTextChange = null
        onEditClick = null
        onActionDone = null
        onBackPress = null
        super.onDetachedFromWindow()
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        val density = getContext().resources.displayMetrics.density
        val typedArray: TypedArray =
            getContext().obtainStyledAttributes(attrs, R.styleable.EditTextRound)

        val textValueColor = typedArray.getColor(R.styleable.EditTextRound_valueColor, ContextCompat.getColor(getContext(),R.color.merchant_color_4a4a4a) )
        binding.edtRound.setTextColor(textValueColor)

        val hintString = typedArray.getString(R.styleable.EditTextRound_android_hint) ?: ""
        binding.edtRound.hint = hintString

        val hintStringColor = typedArray.getColor(R.styleable.EditTextRound_hintColor, ContextCompat.getColor(getContext(),R.color.merchant_color_d0d0d2))
        binding.edtRound.setHintTextColor(hintStringColor)

        val maxLength = typedArray.getInt(R.styleable.EditTextRound_maxLength, 100)
        setMaxLength(maxLength)

        hintFont = getTypeFace(typedArray.getInt(R.styleable.EditTextRound_hintFont,0))
        valueFont = getTypeFace(typedArray.getInt(R.styleable.EditTextRound_valueFont,1))


        val iconLeftDrawable = typedArray.getDrawable(R.styleable.EditTextRound_srcIconLeft)
        if (iconLeftDrawable != null) {
            binding.imgLeft.visibility = View.VISIBLE
            binding.imgLeft.setImageDrawable(iconLeftDrawable)
        }

        val backgroundDrawable = typedArray.getDrawable(R.styleable.EditTextRound_android_background)
        if (backgroundDrawable != null) {
            binding.llRootView.background = backgroundDrawable
        }

        val imeOption = typedArray.getInteger(R.styleable.EditTextRound_android_imeOptions, EditorInfo.IME_ACTION_DONE)
        binding.edtRound.imeOptions = imeOption

        val isShowCancelBtn = typedArray.getBoolean(R.styleable.EditTextRound_showCancelButton, false)
        setShowCancelButton(isShowCancelBtn)

        val inputType = typedArray.getInteger(R.styleable.EditTextRound_android_inputType, InputType.TYPE_CLASS_TEXT)
        binding.edtRound.inputType = inputType

        if (inputType == (InputType.TYPE_NUMBER_VARIATION_PASSWORD + InputType.TYPE_CLASS_NUMBER)
            || inputType == (InputType.TYPE_TEXT_VARIATION_PASSWORD + InputType.TYPE_CLASS_TEXT) ){
            isPassword = true
        }
        binding.edtRound.typeface = hintFont

        binding.edtRound.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (viewText.isNotEmpty()){
                    if (!isHideClear) {
                        binding.imgClear.visibility = View.VISIBLE
                    } else {
                        binding.imgClear.visibility = View.GONE
                    }
                    if (isPassword) {
                        binding.imgShowPassword.visibility = View.VISIBLE
                    }
                }
                binding.edtRound.performClick()
            } else {
                binding.imgClear.visibility = View.VISIBLE
                binding.imgShowPassword.visibility = View.VISIBLE
            }
            onFocusChange?.invoke(hasFocus)
        }
        binding.edtRound.addTextChangedListener(textChangeListener)
//
//        binding.edtRound.setSafeOnClickListener {
//            binding.edtRound.requestFocus()
//            binding.edtRound.showKeyboard()
//            if (isShowCancelBtn && viewText.isEmpty()) {
//                binding.tvCancel.isVisible = true
//            }
//            onEditClick?.invoke()
//        }

//        binding.root.setSafeOnClickListener {
//            binding.edtRound.requestFocus()
//        }
        binding.edtRound.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                onActionDone?.invoke()
                binding.edtRound.clearFocus()
                binding.edtRound.hideKeyboard()
                true
            }
            else {
                false
            }
        }

        binding.imgClear.setOnClickListener {
            binding.edtRound.setText("")
            binding.edtRound.requestFocus()
            onClearData?.invoke()
        }

        binding.tvCancel.setOnClickListener {
            it.isVisible = false
            clearFocusEdittext()
            onCancelButtonClick?.invoke()
        }

        if (isPassword) {
            binding.imgShowPassword.setOnClickListener {
                if ( binding.imgShowPassword.tag == "hide") {
                    binding.edtRound.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    binding.imgShowPassword.setImageResource(com.example.baseapp.R.drawable.ic_show_password)
                    binding.imgShowPassword.tag = "show"
                } else {
                    binding.edtRound.transformationMethod = PasswordTransformationMethod.getInstance()
                    binding.imgShowPassword.setImageResource(com.example.baseapp.R.drawable.ic_icon_hide_password)
                    binding.imgShowPassword.tag = "hide"
                }
            }
        }
        typedArray.recycle()
    }

    fun setDrawableLeft(icon: Drawable?) {
        binding.imgLeft.setImageDrawable(icon)
    }

    fun setDrawableLeft(icon: Int) {
        binding.imgLeft.setImageResource(icon)
    }

    fun setBackground(resourceId: Int) {
        binding.llRootView.setBackgroundResource(resourceId)
    }

    fun setColorText(resourceId: Int) {
        binding.edtRound.setTextColor(resourceId)
    }

    fun setHint(hint: String) {
        binding.edtRound.hint = hint
    }

    fun focusEditText(isFocus: Boolean) {
        if (isFocus) {
            binding.edtRound.requestFocus()
            binding.edtRound.showKeyboard()
        } else {
            binding.edtRound.clearFocus()
            binding.edtRound.hideKeyboard()
        }
    }

    fun editTextAddTextChange() {
        binding.edtRound.requestFocus()
    }

    fun setIconLeftShow(isShow: Boolean) {
        binding.imgLeft.isVisible = isShow
    }

    // set value and show icon
    // sử dụng trong các màn có tìm kiếm
    fun setEditText(message: String) {
        binding.edtRound.removeTextChangedListener(textChangeListener)
        binding.edtRound.setText(message)
        binding.edtRound.setSelection(message.length)
        viewText = message
        binding.edtRound.addTextChangedListener(textChangeListener)
        if (isShowCancelBtn) {
            binding.tvCancel.isVisible = message.isEmpty()
        }
        if (!isHideClear) {
            binding.imgClear.isVisible = message.isNotEmpty()
        }
        if (TextUtils.isEmpty(message)) {
            binding.edtRound.typeface = hintFont
        } else {
            binding.edtRound.typeface = valueFont
        }

    }

    // set value not show icon
    // sử dụng trong các màn nhập liệu thông thường
    fun setEditContent(message: String) {
        binding.edtRound.removeTextChangedListener(textChangeListener)
        binding.edtRound.setText(message)
        viewText = message
        binding.edtRound.addTextChangedListener(textChangeListener)
        if (TextUtils.isEmpty(message)) {
            binding.edtRound.typeface = hintFont
        } else {
            binding.edtRound.typeface = valueFont
        }

    }

    fun setIconLeftClickListener(onSafeClick: (View) -> Unit) {
        binding.imgLeft.setSafeOnClickListener(onSafeClick)
    }

    fun setEditTextClickListener(onSafeClick: (View) -> Unit) {
        binding.edtRound.setSafeOnClickListener(onSafeClick)
    }

    fun setHideClearBtn() {
        isHideClear = true
    }

    fun setEnableEditText(enable: Boolean) {
        binding.edtRound.isEnabled = enable
    }

    fun setFocusableEditText(enable: Boolean) {
        binding.edtRound.isFocusable = enable
    }

    fun setInputType(type: Int){
        binding.edtRound.inputType = type
    }

    fun setVisibleImgShowPassword(isVisible: Boolean){
        binding.imgShowPassword.isVisible = isVisible
    }

    fun clearFocusEdittext(){
        binding.edtRound.clearFocus()
        hideKeyboard()
    }

    fun setSelection(type: Int){
        binding.edtRound.setSelection( type)
    }

    fun getTextEdit(): String{
        return  binding.edtRound.text.toString().trim()
    }

    fun setMaxLength(maxLength: Int) {
        binding.edtRound.filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    private fun getTypeFace(value: Int) : Typeface? {
        var typeface: Typeface? = null
        when (value) {
            0 -> {
                typeface = ResourcesCompat.getFont(context, com.example.baseapp.R.font.sf_lightitalic)

            }
            1 -> {
                typeface = ResourcesCompat.getFont(context, com.example.baseapp.R.font.ssp_bold)

            }
            2 -> {
                typeface = ResourcesCompat.getFont(context, com.example.baseapp.R.font.sf_medium)
            }
            3 -> {
                typeface = ResourcesCompat.getFont(context, com.example.baseapp.R.font.ssp_regular)

            }
            4 -> {
                typeface = ResourcesCompat.getFont(context, R.font.ssp_semi_bold)

            }
        }
        return  typeface
    }

    fun clearText(){
        binding.edtRound.setText("")
    }

    fun editPerformClick(){
        binding.edtRound.performClick()
    }

    fun showClearIcon(isShow: Boolean) {
        binding.imgClear.isVisible = isShow
    }
    fun setShowCancelButton(isShow: Boolean) {
        isShowCancelBtn = isShow
    }
}