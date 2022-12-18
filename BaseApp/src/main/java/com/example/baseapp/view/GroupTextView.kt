package com.example.baseapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.InputFilter
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
import com.example.baseapp.databinding.LayoutGroupTextviewBinding
import com.vnpay.merchant.ui.views.AbstractTextWatcher

class GroupTextView: LinearLayout{
    var onFocusChange: ((Boolean) -> Unit)? = null
    var onTextChange: ((CharSequence?) -> Unit)? = null
    var onClearText: (() -> Unit)? = null
    var onRightTextListener: (() -> Unit)? = null
    var setOnEdittextDone: ((String) -> Unit)? = null

    private var hintText: String? = null
    private var title: String? = null
    private var leftIconRes = 0
    private var rightIconRes = 0
    private var isShowTitleRight : Boolean = false
    private var isShowClear: Boolean = false
    private var contentTextSize = 0f
    private var isEditTextFocusable: Boolean = true
    private lateinit var binding: LayoutGroupTextviewBinding
    private var hintFont: Typeface? = null
    private var valueFont: Typeface? = null
    private var focusBackground: Int = R.drawable.shape_merchant_color_f8f8f9_corner_12_stroke_1_5_004a9c
    private var lostFocusBackground: Int = R.drawable.shape_merchant_color_f8f8f9_corner_12
    private var groupEditTextBackground : Int = 0
    private var editTextBackgroundColor: Int = 0
    private var rightText: String? = null
    private var isEnableEdit : Boolean = true

    private var FONT_BOLD: Typeface? = null
    private var FONT_ITALIC: Typeface? = null
    private var FONT_NORMAL: Typeface? = null
    private var FONT_SEMI: Typeface? = null
    private var FONT_MEDIUM: Typeface? = null
    private var FONT_REGULAR: Typeface? = null
    private var FONT_BLACK: Typeface? = null

    private val textChangeListener = object : AbstractTextWatcher(){
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (s.toString().isNotEmpty()) {
                binding.edtContent.typeface = valueFont
            } else {
                binding.edtContent.typeface = hintFont
            }
            setGoneViews(binding.tvError)
            binding.ivClear.isVisible =
                !s?.toString().isNullOrEmpty() && isShowClear
            onTextChange?.invoke(s)
        }
    }

    private fun setGoneViews(vararg views: View) {
        views.forEach {
            if (it.visibility != View.GONE) it.visibility = View.GONE
        }
    }

    init {
        orientation = VERTICAL
        compound()
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    override fun onDetachedFromWindow() {
        onFocusChange = null
        onClearText = null
        onTextChange = null
        super.onDetachedFromWindow()
    }

    fun setTitle(title: String?) {
        binding.tvTitle.text = title
    }

    fun setHint(hint: String?) {
        binding.edtContent.hint = hint
    }

    fun setValueContent(content: String?) {
        binding.edtContent.setText(content?: "")
        if (content.isNullOrEmpty()) {
            binding.edtContent.typeface = hintFont
        } else {
            binding.edtContent.typeface = valueFont
        }

    }

    fun clearContent() {
        setValueContent("")
    }

    fun setEnableEdittext(enable: Boolean) {
        binding.edtContent.isEnabled = enable
    }

    fun setVisibleMessageError(error: String?) {
        error?.let {
            binding.tvError.isVisible = true
            binding.tvError.text = it
        }
    }

    fun setGoneMessageError() {
        setGoneViews(binding.tvError)
        binding.rlGroupTv.background = ContextCompat.getDrawable(context, focusBackground)
    }

    fun isTvErrorVisible():Boolean {
        return binding.tvError.isVisible
    }

    fun getContentText(): String {
        return binding.edtContent.text.toString().trim()
    }

    fun getEditText(): EditText {
        return binding.edtContent
    }

    fun hasFocusInput(): Boolean {
        return binding.rlGroupTv.requestFocus()
    }


    fun setBackgroundClickListener(onSafeClick: (View) -> Unit) {
        binding.rlGroupTv.setSafeOnClickListener(onSafeClick)
    }

    fun setRightIcon(rightIcon: Int) {
        binding.ivIconRight.isVisible = true
        binding.ivIconRight.setImageResource(rightIcon)
    }

    fun setLeftIcon(leftIcon: Int) {
        binding.ivIconLeft.isVisible = true
        binding.ivIconLeft.setImageResource(leftIcon)
    }

    fun setLeftIconVisible(isVisible: Boolean) {
        binding.ivIconLeft.isVisible = isVisible
    }

    fun setRightIconVisible(isVisible: Int) {
        binding.ivIconRight.visibility = isVisible
    }


    fun setBackground(background: Int) {
        binding.rlGroupTv.setBackgroundResource(background)
    }

    fun setMaxLength(maxLength: Int) {
        binding.edtContent.filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    fun clearText() {
        binding.edtContent.setText("")
        binding.edtContent.clearFocus()
        binding.rlGroupTv.background = ContextCompat.getDrawable(context, lostFocusBackground)
    }

    fun setRequestFocusEdittext(){
        binding.edtContent.requestFocus()
        showKeyboard()
    }

    fun isShowIconClearText(isVisible: Boolean){
        binding.ivClear.isVisible = isVisible
    }

    fun setVisibleRightText(isVisible: Boolean){
        binding.tvRight.isVisible = isVisible
    }

    fun setInputType(inputType : Int){
        binding.edtContent.inputType = inputType
    }

    private fun compound() {
        binding = LayoutGroupTextviewBinding.inflate(LayoutInflater.from(context), this)
    }

    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet?) {
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.GroupEditTextView, 0, 0)
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_left_icon)) {
            leftIconRes = ta.getResourceId(
                R.styleable.GroupEditTextView_gr_left_icon,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_right_icon)) {
            rightIconRes = ta.getResourceId(
                R.styleable.GroupEditTextView_gr_right_icon,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_text_size)) {
            contentTextSize = ta.getDimension(
                R.styleable.GroupEditTextView_gr_text_size,
                0f
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_text_size)) {
            contentTextSize = ta.getDimension(
                R.styleable.GroupEditTextView_gr_text_size,
                0f
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_hint)) {
            hintText = ta.getString(R.styleable.GroupEditTextView_gr_hint)
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_title)) {
            title = ta.getString(R.styleable.GroupEditTextView_gr_title)
        }

        if (ta.hasValue(R.styleable.GroupEditTextView_gr_is_edit_text_focusable)) {
            isEditTextFocusable = ta.getBoolean(R.styleable.GroupEditTextView_gr_is_edit_text_focusable, true)
            binding.edtContent.isFocusable = isEditTextFocusable
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_is_show_clear_text)) {
            isShowClear = ta.getBoolean(
                R.styleable.GroupEditTextView_gr_is_show_clear_text,
                false
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_background_focus)) {
            focusBackground = ta.getResourceId(
                R.styleable.GroupEditTextView_gr_background_focus,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_background_group_edittext_drawable)) {
            groupEditTextBackground = ta.getResourceId(
                R.styleable.GroupEditTextView_gr_background_group_edittext_drawable,
                0
            )
            if(groupEditTextBackground != 0){
                binding.rlGroupTv.setBackgroundResource(groupEditTextBackground)
            }
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_background_edittext_color)) {
            editTextBackgroundColor = ta.getResourceId(
                R.styleable.GroupEditTextView_gr_background_edittext_color,
                0
            )
            if(editTextBackgroundColor != 0){
                binding.edtContent.setBackgroundResource(editTextBackgroundColor)
            }
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_is_show_title_right)) {
            isShowTitleRight = ta.getBoolean(R.styleable.GroupEditTextView_gr_is_show_title_right, false)
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_text_right)) {
            rightText = ta.getString(R.styleable.GroupEditTextView_gr_text_right)
        }
        if (ta.hasValue(R.styleable.GroupEditTextView_gr_is_enable_edit)) {
            isEnableEdit = ta.getBoolean(R.styleable.GroupEditTextView_gr_is_enable_edit, true)
            binding.edtContent.isEnabled = isEnableEdit
        }
        val maxLength = ta.getInt(R.styleable.GroupEditTextView_gr_max_length, 100)
        setMaxLength(maxLength)

        hintFont = getFont(3, context)
        valueFont = getFont(2, context)
        binding.edtContent.addTextChangedListener(textChangeListener)

        initView()
        ta.recycle()
    }

    private fun getFont(type: Int, context: Context): Typeface? {
        return when (type) {
            1 -> {
                if (FONT_MEDIUM == null) {
                    FONT_MEDIUM = ResourcesCompat.getFont(context, R.font.sf_medium)
                }
                FONT_MEDIUM
            }
            2 -> {
                if (FONT_BOLD == null) {
                    FONT_BOLD = ResourcesCompat.getFont(context, R.font.ssp_bold)
                }
                FONT_BOLD
            }
            3 -> {
                if (FONT_ITALIC == null) {
                    FONT_ITALIC = ResourcesCompat.getFont(context, R.font.sf_lightitalic)
                }
                FONT_ITALIC
            }
            else -> {
                if (FONT_REGULAR == null) {
                    FONT_REGULAR = ResourcesCompat.getFont(context, R.font.ssp_regular)
                }
                FONT_REGULAR
            }
        }
    }


    private fun initView() {
        binding.edtContent.hint = hintText ?: ""
        binding.edtContent.typeface = hintFont
        if(!title.isNullOrEmpty()){
            binding.tvTitle.isVisible = true
            binding.tvTitle.text = title ?: ""
        }
        if (contentTextSize != 0f) {
            binding.edtContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize)
        }
        if (leftIconRes != 0) {
            binding.ivIconLeft.isVisible = true
            binding.ivIconLeft.setImageResource(leftIconRes)
        }

        if (rightIconRes != 0) {
            binding.ivIconRight.isVisible = true
            binding.ivIconRight.setImageResource(rightIconRes)
        }
        if (isShowTitleRight){
            binding.tvTitleRight.visibility = View.VISIBLE
        }
        binding.tvRight.text = rightText?:""

        binding.edtContent.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.ivClear.isVisible = isShowClear
                binding.rlGroupTv.background = ContextCompat.getDrawable(context, focusBackground)
            } else {
                binding.ivClear.isVisible = false
                binding.rlGroupTv.background = ContextCompat.getDrawable(context, lostFocusBackground)
            }
            onFocusChange?.invoke(hasFocus)
        }

        binding.edtContent.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setOnEdittextDone?.invoke(binding.edtContent.text.toString())
                binding.edtContent.clearFocus()
                binding.edtContent.hideKeyboard()
                return@OnEditorActionListener true
            }
            false
        })

        binding.ivClear.setSafeOnClickListener {
            binding.edtContent.setText("")
            binding.edtContent.requestFocus()
            binding.edtContent.showKeyboard()
            onClearText?.invoke()
        }
        binding.tvRight.setSafeOnClickListener {
            binding.edtContent.clearFocus()
            hideKeyboard()
            binding.tvRight.isVisible = false
        }
    }
}