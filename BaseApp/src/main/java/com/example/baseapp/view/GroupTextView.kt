package com.example.baseapp.view

import android.content.Context
import android.graphics.Typeface
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.baseapp.R
import com.example.baseapp.UtilsBase
import com.example.baseapp.databinding.LayoutGroupTextviewBinding
import com.vnpay.merchant.ui.views.AbstractTextWatcher


class GroupTextView: LinearLayout{
    var onFocusChange: ((Boolean) -> Unit)? = null
    var onTextChange: ((CharSequence?) -> Unit)? = null
    var onClearText: (() -> Unit)? = null
    var onEditTextClick: (() -> Unit)? = null
    var onRightTextListener: (() -> Unit)? = null

    private var hintText: String? = null
    private var textContent: String? = null
    private var title: String? = null
    private var leftIconRes = 0
    private var rightIconRes = 0
    private var isRequiredLabel : Boolean = false
    private var isTitleRightHtml : Boolean = false
    private var isShowClear: Boolean = false
    private var contentTextSize = 0f
    private var isEditTextFocusable: Boolean = true
    private var isGoneTitle: Boolean = false
    private lateinit var binding: LayoutGroupTextviewBinding
    private var hintFont: Typeface? = null
    private var valueFont: Typeface? = null
    private var focusBackground: Int = R.drawable.shape_ffffff_stroke_004a9c_corner_12
    private var lostFocusBackground: Int = R.drawable.shape_merchant_color_f8f8f9_corner_12
    private var groupEditTextBackground : Int = 0
    private var editTextBackgroundColor: Int = 0
    private var rightText: String? = null

    private val textChangeListener = object :AbstractTextWatcher(){
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (s.toString().isNotEmpty()) {
                binding.edtContent.typeface = valueFont
                if(!rightText.isNullOrEmpty()){
                    binding.tvRight.isVisible = false
                }
            } else {
                if(!rightText.isNullOrEmpty()){
                    binding.tvRight.isVisible = true
                }
                binding.edtContent.typeface = hintFont
            }
            UtilsBase.g().setGoneViews(binding.tvError)
            binding.ivClear.isVisible =
                !s?.toString()?.trim().isNullOrEmpty() && isShowClear
            onTextChange?.invoke(s.toString().trim())
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

    fun setTitleHtml(title: String?) {
        binding.tvTitle.text = title?.html()
    }

    fun setVisibleTitle(isVisible: Boolean) {
        binding.tvTitle.isVisible = isVisible
    }

    fun setHint(hint: String?) {
        binding.edtContent.hint = hint
    }

    fun setValueContent(content: String?) {
        binding.edtContent.removeTextChangedListener(textChangeListener)
        binding.edtContent.setText(content ?: "")
        binding.edtContent.addTextChangedListener(textChangeListener)
        if (content.isNullOrEmpty()) {
            binding.edtContent.typeface = hintFont
        } else {
            binding.edtContent.typeface = valueFont
        }

    }

    fun setInputType(type: Int) {
        binding.edtContent.inputType = type
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
        UtilsBase.g().setGoneViews(binding.tvError)
    }

    fun getContentText(): String {
        return binding.edtContent.text.toString().trim()
    }

    fun hasFocusInput(): Boolean {
        return binding.rlGroupTv.requestFocus()
    }

    fun setFocusableEditText(enable: Boolean) {
        binding.edtContent.isFocusable = enable
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

    fun isTvErrorVisible(): Boolean{
        return binding.tvError.isVisible
    }

    fun getEditText() : EditText{
        return binding.edtContent
    }

    fun setRequestFocusEdittext(){
        binding.edtContent.requestFocus()
    }

    fun clearFocusEdittext(){
        binding.edtContent.clearFocus()
        hideKeyboard()
    }

    fun setVisibleRightText(isVisible: Boolean){
        binding.tvRight.isVisible = isVisible
    }

    fun setRightTextClickListener(onSafeClick: (View) -> Unit) {
        binding.tvRight.setSafeOnClickListener(onSafeClick)
    }
    fun showClearIcon(isShow: Boolean) {
        binding.ivClear.isVisible = isShow
    }
    fun focusEditText(isFocus: Boolean) {
        if (isFocus) {
            binding.edtContent.requestFocus()
        } else {
            binding.edtContent.clearFocus()
            binding.edtContent.hideKeyboard()
        }
    }

    private fun compound() {
        binding = LayoutGroupTextviewBinding.inflate(LayoutInflater.from(context), this)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.GroupTextView, 0, 0)
        if (ta.hasValue(R.styleable.GroupTextView_gr_left_icon)) {
            leftIconRes = ta.getResourceId(
                R.styleable.GroupTextView_gr_left_icon,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_right_icon)) {
            rightIconRes = ta.getResourceId(
                R.styleable.GroupTextView_gr_right_icon,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_text_size)) {
            contentTextSize = ta.getDimension(
                R.styleable.GroupTextView_gr_text_size,
                0f
            )
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_text_size)) {
            contentTextSize = ta.getDimension(
                R.styleable.GroupTextView_gr_text_size,
                0f
            )
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_hint)) {
            hintText = ta.getString(R.styleable.GroupTextView_gr_hint)
        }

        if(ta.hasValue(R.styleable.GroupTextView_gr_text)) {
            textContent = ta.getString(R.styleable.GroupTextView_gr_text)
        }

        if (ta.hasValue(R.styleable.GroupTextView_gr_title)) {
            title = ta.getString(R.styleable.GroupTextView_gr_title)
        }

        if (ta.hasValue(R.styleable.GroupTextView_gr_is_edit_text_focusable)) {
            isEditTextFocusable = ta.getBoolean(R.styleable.GroupTextView_gr_is_edit_text_focusable, true)
            binding.edtContent.isFocusable = isEditTextFocusable
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_is_show_clear_text)) {
            isShowClear = ta.getBoolean(
                R.styleable.GroupTextView_gr_is_show_clear_text,
                false
            )
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_background_focus)) {
            focusBackground = ta.getResourceId(
                R.styleable.GroupTextView_gr_background_focus,
                0
            )
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_background_group_edittext_drawable)) {
            groupEditTextBackground = ta.getResourceId(
                R.styleable.GroupTextView_gr_background_group_edittext_drawable,
                0
            )
            if(groupEditTextBackground != 0){
                binding.rlGroupTv.setBackgroundResource(groupEditTextBackground)
            }
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_background_edittext_color)) {
            editTextBackgroundColor = ta.getResourceId(
                R.styleable.GroupTextView_gr_background_edittext_color,
                0
            )
            if(editTextBackgroundColor != 0){
                binding.edtContent.setBackgroundResource(editTextBackgroundColor)
            }
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_is_required_label)) {
            isRequiredLabel = ta.getBoolean(R.styleable.GroupTextView_gr_is_required_label, false)
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_text_right)) {
            rightText = ta.getString(R.styleable.GroupTextView_gr_text_right)
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_is_gone_title)) {
            isGoneTitle = ta.getBoolean(R.styleable.GroupTextView_gr_is_gone_title, false)
        }
        if (ta.hasValue(R.styleable.GroupTextView_gr_is_title_html)) {
            isTitleRightHtml = ta.getBoolean(R.styleable.GroupTextView_gr_is_title_html, false)
        }
        if(isGoneTitle){
            binding.tvTitle.visibility = View.GONE
        }
        val maxLength = ta.getInt(R.styleable.GroupTextView_gr_max_length, 100)
        setMaxLength(maxLength)

        val inputType = ta.getInt(R.styleable.GroupTextView_android_inputType, InputType.TYPE_CLASS_TEXT)
        setInputType(inputType)

        hintFont = UtilsBase.g().getFont(1, context)
        valueFont = UtilsBase.g().getFont(2, context)
        binding.edtContent.addTextChangedListener(textChangeListener)

        initView()
        ta.recycle()
    }


    private fun initView() {
        binding.edtContent.hint = hintText ?: ""
        binding.edtContent.typeface = hintFont
        binding.tvTitle.text = title ?: ""
        setValueContent(textContent)

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
        if (isRequiredLabel){
            binding.tvRequiredLabel.visibility = View.VISIBLE
        }

        binding.tvRight.text = rightText?:""

        if(isTitleRightHtml){
            binding.tvTitle.text = title?.html()
        }
        binding.edtContent.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if(!rightText.isNullOrEmpty()){
                    binding.tvRight.isVisible = true
                    binding.ivClear.isVisible = false
                }
                binding.rlGroupTv.background = ContextCompat.getDrawable(context, focusBackground)
            } else {
                binding.tvRight.isVisible = false
                binding.rlGroupTv.background = ContextCompat.getDrawable(context, lostFocusBackground)
            }
            onFocusChange?.invoke(hasFocus)
        }
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
        binding.llGroupTv.setSafeOnClickListener {
            binding.edtContent.requestFocus()
            binding.edtContent.showKeyboard()
            onEditTextClick?.invoke()
        }
    }
}