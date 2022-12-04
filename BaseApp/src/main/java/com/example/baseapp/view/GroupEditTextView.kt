package com.example.baseapp.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.baseapp.R
import com.example.baseapp.captures.MoneyCapture
import com.example.baseapp.databinding.LayoutGroupEdittextBinding

class GroupEditTextView(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    var onAfterTextChange: ((String) -> Unit)? = null
    var setOnEdittextDone: ((String) -> Unit)? = null
    var onFocusChange: ((Boolean) -> Unit)? = null
    var clear: (() -> Unit)? = null
    var onRightEditAction: (() -> Unit)? = null
    private var leftIconRes = 0
    private var rightIconRes = 0
    private var leftIconWidth = 0
    private var leftIconHeight = 0
    private var rightIconWidth = 0
    private var rightIconHeight = 0
    private var maxLength = 0
    private var isShowClear: Boolean = false
    private var isShowMarkOn: Boolean = false
    private var hintText: String? = null
    private var hintTextColor: Int = ContextCompat.getColor(context, R.color.color_text_8)
    private var hintTextColorDisable: Int = ContextCompat.getColor(context, R.color.color_text_7)
    private var contentTextColor: Int = ContextCompat.getColor(context, R.color.color_text_6)
    private var contentTextColorDisable: Int = ContextCompat.getColor(context, R.color.color_text_7)
    private var contentTextSize = 0f
    private var focusBackground: Int = R.drawable.shape_white_bg_blue_stroke_radius_8
    private var lostFocusBackground: Int = R.drawable.background_white_stroke_color_dash_radius_8
    private var rightTextColor: Int = ContextCompat.getColor(context, R.color.merchant_color_004a9c)
    private var rightTextSize = 0f
    private var rightText: String? = null
    private var rightTextEdit: String? = null
    private var isEnableEdit: Boolean = true
    private lateinit var binding: LayoutGroupEdittextBinding

    init {
        orientation = VERTICAL
        compound()
        init(attrs)
        initView()
    }

    fun setGroupClickListener(onSafeClick: (View) -> Unit) {
        binding.viewParent.setSafeOnClickListener(onSafeClick)
        binding.edtGroupEditTextLayout.setSafeOnClickListener(onSafeClick)
    }

    fun setHint(hint: String?) {
        binding.edtGroupEditTextLayout.hint = hint
    }

    fun setFontHintText(fontType: Typeface) {
        binding.edtGroupEditTextLayout.typeface = fontType
    }

    fun setValueText(content: String?) {
        binding.edtGroupEditTextLayout.setText(content)
    }

    fun setSelectionInput(position: Int) {
        binding.edtGroupEditTextLayout.setSelection(position)
    }

    fun setInputType(inputType: Int) {
        binding.edtGroupEditTextLayout.inputType = inputType
    }

    fun setVisibleMessageError(error: String?) {
        error?.let {
            binding.tvError.isVisible = true
            binding.tvError.text = it
            binding.viewParent.background = ContextCompat.getDrawable(context, lostFocusBackground)
        }
    }

    fun setGoneMessageError() {
        if (binding.tvError.isVisible) {
            binding.tvError.isVisible = false
            binding.viewParent.background = ContextCompat.getDrawable(context, focusBackground)
        }
    }

    fun getValueText(): String {
        return binding.edtGroupEditTextLayout.text.toString().trim()
    }

    fun getEditText(): EditText {
        return binding.edtGroupEditTextLayout
    }

    fun clearEditText() {
        binding.edtGroupEditTextLayout.setText("")
        binding.edtGroupEditTextLayout.clearFocus()
    }

    fun hasFocusInput(): Boolean {
        return binding.edtGroupEditTextLayout.hasFocus()
    }

    fun setRightIconClickListener(onSafeClick: (View) -> Unit) {
        binding.ivRightIcon.setSafeOnClickListener(onSafeClick)
    }

    fun setLeftIconClickListener(onSafeClick: (View) -> Unit) {
        binding.ivLeftIcon.setSafeOnClickListener(onSafeClick)
    }

    fun setRightIcon(rightIcon: Int) {
        binding.ivRightIcon.isVisible = true
        binding.ivRightIcon.setImageResource(rightIcon)
    }

    fun showRightText(isShow: Boolean) {
        binding.tvRight.isVisible = isShow
    }

    fun enableEdit(isEnabled: Boolean, textColor: Int = 0) {
        if (!isEnabled) {
            this.isEnableEdit = isEnabled
            binding.ivClear.isVisible = false
            enableFocusInput(false)
            setBackground(R.drawable.bg_color_search_radius_8)
            setTextColor(contentTextColorDisable)
            setTextColorHint(hintTextColorDisable)
        } else if (binding.tvRight.text == context.getString(R.string.save_title)) {
            this.isEnableEdit = true
            enableFocusInput(true)
            binding.ivClear.isVisible = getValueText().isNotEmpty() && isShowClear
            setBackground(R.drawable.background_white_stroke_color_dash_radius_8)
            setTextColor(contentTextColor)
            setTextColorHint(hintTextColor)
        } else {
            this.isEnableEdit = false
            enableFocusInput(false)
            binding.ivClear.isVisible = false
            setBackground(R.drawable.background_white_stroke_color_dash_radius_8)
            setTextColor(contentTextColor)
            setTextColorHint(hintTextColor)
        }
        if (textColor != 0) {
            setTextColor(textColor)
        }
    }

    fun setBackground(background: Int) {
        binding.viewParent.setBackgroundResource(background)
    }

    fun setTextColor(color: Int) {
        binding.edtGroupEditTextLayout.setTextColor(color)
    }

    fun setTextColorHint(color: Int) {
        binding.edtGroupEditTextLayout.setTextColor( ColorStateList.valueOf(color))
    }

    fun enableFocusInput(focusable: Boolean) {
        binding.edtGroupEditTextLayout.isFocusable = focusable
        binding.edtGroupEditTextLayout.isFocusableInTouchMode = focusable
    }

    fun enableInput(enable: Boolean){
        binding.edtGroupEditTextLayout.isEnabled = enable
        binding.viewParent.isEnabled = enable
    }

    fun setLeftIcon(leftIcon: Int) {
        binding.ivLeftIcon.isVisible = true
        binding.ivLeftIcon.setImageResource(leftIcon)
    }

    fun addMoneyCapture(maxLength: Int = 10) {
        binding.edtGroupEditTextLayout.addTextChangedListener(
            MoneyCapture(
                binding.edtGroupEditTextLayout,
                maxLength
            )
        )
    }

    fun showKeyBoard() {
        binding.edtGroupEditTextLayout.requestFocus()
        binding.edtGroupEditTextLayout.showKeyboard()
    }

    fun hideKeyboard() {
        binding.edtGroupEditTextLayout.hideKeyboard()
    }

    fun clearText() {
        binding.edtGroupEditTextLayout.setText("")
        binding.edtGroupEditTextLayout.clearFocus()
        binding.viewParent.background = ContextCompat.getDrawable(context, lostFocusBackground)
    }

    fun setMaxLength(length: Int) {
        binding.edtGroupEditTextLayout.filters =
            binding.edtGroupEditTextLayout.filters + InputFilter.LengthFilter(length)
    }

    private fun compound() {
        binding = LayoutGroupEdittextBinding.inflate(LayoutInflater.from(context), this)
    }

    private fun init(attrs: AttributeSet?) {
        val ta =
            context.theme.obtainStyledAttributes(attrs, R.styleable.GroupEditTextView, 0, 0)
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_left_icon)) {
            leftIconRes = ta.getResourceId(
                R.styleable.GroupEditTextView_gr_left_icon,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_left_icon_width)) {
            leftIconWidth = ta.getDimensionPixelSize(
                R.styleable.GroupEditTextView_gr_left_icon_width,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_left_icon_height)) {
            leftIconHeight = ta.getDimensionPixelSize(
                R.styleable.GroupEditTextView_gr_left_icon_height,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_right_icon)) {
            rightIconRes = ta.getResourceId(
                R.styleable.GroupEditTextView_gr_right_icon,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_right_icon_width)) {
            rightIconWidth = ta.getDimensionPixelSize(
                R.styleable.GroupEditTextView_gr_right_icon_width,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_right_icon_height)) {
            rightIconHeight = ta.getDimensionPixelSize(
                R.styleable.GroupEditTextView_gr_right_icon_height,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_is_show_clear_text)) {
            isShowClear = ta.getBoolean(
                R.styleable.GroupEditTextView_gr_is_show_clear_text,
                false
            )
        }

        if (ta.hasValue(R.styleable.GroupEditTextView_gr_is_show_mark_on)) {
            isShowMarkOn = ta.getBoolean(
                R.styleable.GroupEditTextView_gr_is_show_mark_on,
                false
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_hint)) {
            hintText = ta.getString(R.styleable.GroupEditTextView_gr_hint)
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_hint_text_color)) {
            hintTextColor = ta.getColor(
                R.styleable.GroupEditTextView_gr_hint_text_color,
                ContextCompat.getColor(getContext(),R.color.merchant_color_d0d0d2)
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_text_color)) {
            contentTextColor =
                ta.getColor(
                    R.styleable.GroupEditTextView_gr_text_color,
                    ContextCompat.getColor(getContext(),R.color.merchant_color_4a4a4a)
                )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_text_size)) {
            contentTextSize = ta.getDimension(
                R.styleable.GroupEditTextView_gr_text_size,
                0f
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_max_length)) {
            maxLength = ta.getInt(
                R.styleable.GroupEditTextView_gr_max_length,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_background_focus)) {
            focusBackground = ta.getResourceId(
                R.styleable.GroupEditTextView_gr_background_focus,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_background_lost_focus)) {
            lostFocusBackground = ta.getResourceId(
                R.styleable.GroupEditTextView_gr_background_lost_focus,
                0
            )
        }

        if (ta.hasValue(R.styleable.GroupEditTextView_gr_right_text_color)) {
            rightTextColor =
                ta.getColor(
                    R.styleable.GroupEditTextView_gr_right_text_color,
                    0
                )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_right_text_size)) {
            rightTextSize = ta.getDimension(
                R.styleable.GroupEditTextView_gr_right_text_size,
                0f
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_right_text_edit)) {
            rightTextEdit = ta.getString(R.styleable.GroupEditTextView_gr_right_text_edit)
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_right_text)) {
            rightText = ta.getString(R.styleable.GroupEditTextView_gr_right_text)
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_is_enable_edit)) {
            isEnableEdit = ta.getBoolean(R.styleable.GroupEditTextView_gr_is_enable_edit, true)
        }
        val inputType = ta.getInteger(
            R.styleable.GroupEditTextView_android_inputType,
            InputType.TYPE_CLASS_TEXT
        )
        binding.edtGroupEditTextLayout.inputType = inputType

        val imeOption = ta.getInteger(
            R.styleable.GroupEditTextView_android_imeOptions,
            EditorInfo.IME_ACTION_DONE
        )
        binding.edtGroupEditTextLayout.imeOptions = imeOption
        ta.recycle()
    }
    private fun initView() {
        binding.viewParent.background = ContextCompat.getDrawable(context, lostFocusBackground)
        binding.edtGroupEditTextLayout.hint = hintText ?: ""
        binding.edtGroupEditTextLayout.typeface = getTypeFace(0)
        if (hintTextColor != 0) {
            binding.edtGroupEditTextLayout.setHintTextColor(hintTextColor)
        }
        if (contentTextColor != 0) {
            binding.edtGroupEditTextLayout.setTextColor(contentTextColor)
        }
        if (maxLength != 0) {
            binding.edtGroupEditTextLayout.filters =
                binding.edtGroupEditTextLayout.filters + InputFilter.LengthFilter(maxLength)
        }
        if (contentTextSize != 0f) {
            binding.edtGroupEditTextLayout.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize)
        }
        if (leftIconRes != 0) {
            binding.ivLeftIcon.isVisible = true
            binding.ivLeftIcon.setImageResource(leftIconRes)
            if (leftIconWidth != 0 && leftIconHeight != 0) {
                val icLayoutParams =
                    binding.ivLeftIcon.layoutParams as LayoutParams
                icLayoutParams.width = leftIconWidth
                icLayoutParams.height = leftIconHeight
                binding.ivLeftIcon.layoutParams = icLayoutParams
            }
        }

        if (isShowMarkOn) {
            binding.cbMarkOnCheck.isVisible = true
        }

        if (rightIconRes != 0) {
            binding.ivRightIcon.isVisible = true
            binding.ivRightIcon.setImageResource(rightIconRes)
            if (rightIconWidth != 0 && rightIconHeight != 0) {
                val icLayoutParams =
                    binding.ivRightIcon.layoutParams as LayoutParams
                icLayoutParams.width = rightIconWidth
                icLayoutParams.height = rightIconHeight
                binding.ivRightIcon.layoutParams = icLayoutParams
            }
        }
        binding.edtGroupEditTextLayout.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setOnEdittextDone?.invoke(binding.edtGroupEditTextLayout.text.toString())
                binding.edtGroupEditTextLayout.clearFocus()
                binding.edtGroupEditTextLayout.hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })
        binding.edtGroupEditTextLayout.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.ivClear.isVisible = isShowClear && isEnableEdit && getValueText().isNotEmpty()
                binding.viewParent.background = ContextCompat.getDrawable(context, focusBackground)
            } else {
                binding.ivClear.isVisible = false
                binding.viewParent.background =
                    ContextCompat.getDrawable(context, lostFocusBackground)
            }
            onFocusChange?.invoke(hasFocus)
        }
        binding.edtGroupEditTextLayout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               if(s.isNullOrEmpty()){
                   binding.edtGroupEditTextLayout.typeface = getTypeFace(0)
               }
                else  binding.edtGroupEditTextLayout.typeface = getTypeFace(1)
            }

            override fun afterTextChanged(s: Editable?) {
                setGoneMessageError()
                binding.ivClear.isVisible =
                    !s?.toString().isNullOrEmpty() && isShowClear && isEnableEdit
                onAfterTextChange?.invoke(s?.toString()?.trim() ?: "")
            }
        })
        binding.ivClear.setOnClickListener {
            binding.edtGroupEditTextLayout.setText("")
            binding.edtGroupEditTextLayout.requestFocus()
            binding.edtGroupEditTextLayout.showKeyboard()
            clear?.invoke()
        }
        binding.cbMarkOnCheck.setOnCheckedChangeListener(MarkOnCheckedChangeListener(binding.edtGroupEditTextLayout, binding.cbMarkOnCheck))
        if (!isEnableEdit) {
            enableFocusInput(false)
        }
        if (rightTextColor != 0) {
            binding.tvRight.setTextColor(rightTextColor)
        }
        if (rightTextSize != 0f) {
            binding.tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize)
        }
        if (!rightText.isNullOrEmpty()) {
            binding.tvRight.isVisible = true
            binding.tvRight.text = rightText
        }
        binding.tvRight.setSafeOnClickListener {
            isEnableEdit = !isEnableEdit
            enableFocusInput(isEnableEdit)
            if (isEnableEdit) {
                binding.edtGroupEditTextLayout.requestFocus()
                binding.edtGroupEditTextLayout.requestFocusFromTouch()
                binding.edtGroupEditTextLayout.showKeyboard()
                binding.tvRight.text = rightTextEdit
                binding.ivClear.isVisible =
                    isShowClear && isEnableEdit && getValueText().isNotEmpty()
            } else {
                onRightEditAction?.invoke()
                binding.tvRight.text = rightText
                binding.ivClear.isVisible = false
                binding.edtGroupEditTextLayout.hideKeyboard()
            }
        }
    }
    private fun getTypeFace(value: Int) : Typeface? {
        var typeface: Typeface? = null
        when (value) {
            0 -> {
                typeface = ResourcesCompat.getFont(context, R.font.sf_regular)

            }
            1 -> {
                typeface = ResourcesCompat.getFont(context, com.example.baseapp.R.font.ssp_bold)

            }
            2 -> {
                typeface = ResourcesCompat.getFont(context, com.example.baseapp.R.font.sf_medium)
            }
            3 -> {
                typeface = ResourcesCompat.getFont(context, com.example.baseapp.R.font.sf_lightitalic)

            }
            4 -> {
                typeface = ResourcesCompat.getFont(context, R.font.ssp_semibold)

            }
        }
        return  typeface
    }
}