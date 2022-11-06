package com.vnpay.merchant.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import com.example.baseapp.R
import com.example.baseapp.view.showKeyboard
import kotlinx.android.synthetic.main.custom_layout_edittext_password.view.*

class EditTextPassword : RelativeLayout {
    var onClearData: (() -> Unit)? = null
    var onBackPress: (() -> Unit)? = null
    var onActionDone: (() -> Unit)? = null
    var onTextChange: ((CharSequence?) -> Unit)? = null
//    var binding: CustomLayoutEdittextPasswordBinding? = null

    val textChangeListener = object :AbstractTextWatcher(){
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
            } else {
                imgClear.visibility = View.GONE
                imgShowPassword.visibility = View.GONE
            }
            onTextChange?.invoke(s)
        }

    }

    private var isPassword = false

    private var isHideClear = false

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
        inflate(context, R.layout.custom_layout_edittext_password, this)
       // binding = com.example.baseapp.databinding.CustomLayoutEdittextPasswordBinding.inflate(LayoutInflater.from(context), this, true)
    //    Log.d(TAG, "initssssssssss: $binding")
        val density = getContext().resources.displayMetrics.density
        val typedArray: TypedArray =
            getContext().obtainStyledAttributes(attrs, R.styleable.EdittextInputAttrs)

        val textValueColor = typedArray.getColor(R.styleable.EdittextInputAttrs_android_textColor, Color.GRAY)
        edtInput.setTextColor(textValueColor)

        val hintString = typedArray.getString(R.styleable.EdittextInputAttrs_android_hint) ?: ""
        edtInput.hint =hintString

        val hintStringColor = typedArray.getColor(R.styleable.EdittextInputAttrs_android_textColorHint, Color.GRAY)
         edtInput.setHintTextColor(hintStringColor)

        val iconLeftDrawable = typedArray.getDrawable(R.styleable.EdittextInputAttrs_srcIconLeft)
        if (iconLeftDrawable != null) {
             imgLeft.visibility = View.VISIBLE
             imgLeft.setImageDrawable(iconLeftDrawable)
        }

        val backgroundDrawable = typedArray.getDrawable(R.styleable.EdittextInputAttrs_android_background)
        if (backgroundDrawable != null) {
             llRootView.background = backgroundDrawable
        }

        val imeOption = typedArray.getInteger(R.styleable.EdittextInputAttrs_android_imeOptions, EditorInfo.IME_ACTION_DONE)
         edtInput.imeOptions = imeOption

        val inputType = typedArray.getInteger(R.styleable.EdittextInputAttrs_android_inputType, InputType.TYPE_CLASS_TEXT)
         edtInput.inputType = inputType

        if (inputType == (InputType.TYPE_NUMBER_VARIATION_PASSWORD + InputType.TYPE_CLASS_NUMBER)
            || inputType == (InputType.TYPE_TEXT_VARIATION_PASSWORD + InputType.TYPE_CLASS_TEXT) ){
            isPassword = true
        }

         edtInput.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (!isHideClear){
                         imgClear.visibility = View.VISIBLE
                    }else {
                         imgClear.visibility = View.GONE
                    }
                    if (isPassword){
                         imgShowPassword.visibility = View.VISIBLE
                    }
                }else {
                     imgClear.visibility = View.GONE
                      imgShowPassword.visibility = View.GONE
                }

        }
         edtInput.addTextChangedListener(textChangeListener)

         edtInput.setOnClickListener {
             edtInput.requestFocus()
             edtInput.showKeyboard()
        }
         edtInput.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                onActionDone?.invoke()
                true
            } else {
                false
            }
        }

         imgClear.setOnClickListener {
             edtInput.setText("")
            onClearData?.invoke()
        }

        if (isPassword) {
             imgShowPassword.setOnClickListener {
                if ( imgShowPassword.tag == "hide") {
                     edtInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                     imgShowPassword.setImageResource(R.drawable.ic_baseline_visibility_24)
                     imgShowPassword.tag = "show"
                } else {
                     edtInput.transformationMethod = PasswordTransformationMethod.getInstance()
                     imgShowPassword.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                     imgShowPassword.tag = "hide"
                }
            }
        }
    }

    fun setDrawableLeft(icon: Drawable?) {
         imgLeft.setImageDrawable(icon)
    }

    fun setDrawableLeft(icon: Int) {
         imgLeft.setImageResource(icon)
    }

    fun setBackground(resourceId: Int) {
         llRootView.setBackgroundResource(resourceId)
    }

    fun setColorText(resourceId: Int) {
         edtInput.setTextColor(resourceId)
    }

    fun setEditText(message: String) {
         edtInput.removeTextChangedListener(textChangeListener)
         edtInput.setText(message)
         edtInput.addTextChangedListener(textChangeListener)
    }

    fun setHideClearBtn() {
        isHideClear = true
    }

    fun getTextEdit(): String{
        return  edtInput.text.toString()
    }
}