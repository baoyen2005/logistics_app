package com.example.baseapp.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.TintTypedArray
import androidx.core.graphics.ColorUtils

object ThemeUtils {
    val VECTOR_DRAWABLE_CLAZZ_NAME = "android.graphics.drawable.VectorDrawable"

    private val TL_TYPED_VALUE = ThreadLocal<TypedValue>()

    internal val DISABLED_STATE_SET = intArrayOf(-android.R.attr.state_enabled)
    internal val FOCUSED_STATE_SET = intArrayOf(android.R.attr.state_focused)
    internal val ACTIVATED_STATE_SET = intArrayOf(android.R.attr.state_activated)
    internal val PRESSED_STATE_SET = intArrayOf(android.R.attr.state_pressed)
    internal val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    internal val SELECTED_STATE_SET = intArrayOf(android.R.attr.state_selected)
    internal val NOT_PRESSED_OR_FOCUSED_STATE_SET =
        intArrayOf(-android.R.attr.state_pressed, -android.R.attr.state_focused)
    internal val EMPTY_STATE_SET = IntArray(0)

    private val TEMP_ARRAY = IntArray(1)

    private val typedValue: TypedValue
        get() {
            var typedValue = TL_TYPED_VALUE.get()
            if (typedValue == null) {
                typedValue = TypedValue()
                TL_TYPED_VALUE.set(typedValue)
            }
            return typedValue
        }

    fun getDrawable(ctx: Context, id: Int): Drawable? {
        if (id == 0)
            return null
        try {
            val drawable = AppCompatResources.getDrawable(ctx, id)
            ThemeUtils.fixDrawable(drawable)

            return drawable
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun fixDrawable(drawable: Drawable?) {
        if (drawable != null) {
            if (Build.VERSION.SDK_INT == 21 && VECTOR_DRAWABLE_CLAZZ_NAME == drawable.javaClass.name) {
                fixVectorDrawableTinting(drawable)
            }
        }
    }

    private fun fixVectorDrawableTinting(drawable: Drawable) {
        val originalState = drawable.state
        if (originalState == null || originalState.size == 0) {
            // The drawable doesn't have a state, so set it to be checked
            drawable.state = ThemeUtils.CHECKED_STATE_SET
        } else {
            // Else the drawable does have a state, so clear it
            drawable.state = ThemeUtils.EMPTY_STATE_SET
        }
        // Now set the original state
        drawable.state = originalState
    }

    fun createDisabledStateList(textColor: Int, disabledTextColor: Int): ColorStateList {
        // Now create a new ColorStateList with the default color, and the new disabled
        // color
        val states = arrayOfNulls<IntArray>(2)
        val colors = IntArray(2)
        var i = 0

        // Disabled state
        states[i] = DISABLED_STATE_SET
        colors[i] = disabledTextColor
        i++

        // Default state
        states[i] = EMPTY_STATE_SET
        colors[i] = textColor
        i++

        return ColorStateList(states, colors)
    }

    fun getThemeAttrColor(context: Context, attr: Int): Int {
        TEMP_ARRAY[0] = attr
        val a = TintTypedArray.obtainStyledAttributes(context, null, TEMP_ARRAY)
        try {
            return a.getColor(0, 0)
        } finally {
            a.recycle()
        }
    }

    fun getThemeAttrColorStateList(context: Context, attr: Int): ColorStateList? {
        TEMP_ARRAY[0] = attr
        val a = TintTypedArray.obtainStyledAttributes(context, null, TEMP_ARRAY)
        try {
            return a.getColorStateList(0)
        } finally {
            a.recycle()
        }
    }

    fun getDisabledThemeAttrColor(context: Context, attr: Int): Int {
        val csl = getThemeAttrColorStateList(context, attr)
        if (csl != null && csl.isStateful) {
            // If the CSL is stateful, we'll assume it has a disabled state and use it
            return csl.getColorForState(DISABLED_STATE_SET, csl.defaultColor)
        } else {
            // Else, we'll generate the color using disabledAlpha from the theme

            val tv = typedValue
            // Now retrieve the disabledAlpha value from the theme
            context.getTheme().resolveAttribute(android.R.attr.disabledAlpha, tv, true)
            val disabledAlpha = tv.float

            return getThemeAttrColor(context, attr, disabledAlpha)
        }
    }

    internal fun getThemeAttrColor(context: Context, attr: Int, alpha: Float): Int {
        val color = getThemeAttrColor(context, attr)
        val originalAlpha = Color.alpha(color)
        return ColorUtils.setAlphaComponent(color, Math.round(originalAlpha * alpha))
    }
}
