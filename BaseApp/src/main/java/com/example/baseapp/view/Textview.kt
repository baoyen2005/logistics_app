package com.example.baseapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.baseapp.R

class Textview : AppCompatTextView {
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        init(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(attrs)
    }


    @SuppressLint("CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.TextView)
            var left: Drawable? = null
            var top: Drawable? = null
            var right: Drawable? = null
            var bottom: Drawable? = null
            if (a.hasValue(R.styleable.TextView_background)) {
                setBackgroundDrawable(
                    ThemeUtils.getDrawable(
                        context,
                        a.getResourceId(R.styleable.TextView_background, 0)
                    )
                )
            }
            var found = false
            if (a.hasValue(R.styleable.TextView_left)) {
                left = ThemeUtils.getDrawable(context, a.getResourceId(R.styleable.TextView_left, 0))
                found = true
            }
            if (a.hasValue(R.styleable.TextView_top)) {
                top = ThemeUtils.getDrawable(context, a.getResourceId(R.styleable.TextView_top, 0))
                found = true
            }
            if (a.hasValue(R.styleable.TextView_right)) {
                right = ThemeUtils.getDrawable(context, a.getResourceId(R.styleable.TextView_right, 0))
                found = true
            }
            if (a.hasValue(R.styleable.TextView_bottom)) {
                bottom = ThemeUtils.getDrawable(context, a.getResourceId(R.styleable.TextView_bottom, 0))
                found = true
            }

            if (a.hasValue(R.styleable.TextView_check) && a.getBoolean(R.styleable.TextView_check, false)) {
                isSelected = true
            }
            if (a.hasValue(R.styleable.TextView_html)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    text = Html.fromHtml(a.getString(R.styleable.TextView_html), Html.FROM_HTML_MODE_COMPACT)
                } else {
                    text = Html.fromHtml(a.getString(R.styleable.TextView_html))
                }
            }
            if (found)
                setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
            a.recycle()
        }
        includeFontPadding = false
        val typeface = typeface
    }
}