package com.example.baseapp.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.SystemClock
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.vnpay.merchant.utils.UtilsBase
import org.json.JSONObject
import org.koin.androidx.scope.BuildConfig
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

fun RecyclerView.setDivider(
    @DrawableRes drawableRes: Int,
    dividerItemDecoration: Int = DividerItemDecoration.VERTICAL
) {
    val divider = DividerItemDecoration(
        this.context,
        dividerItemDecoration
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}

fun Exception.safeLog() {
    if (BuildConfig.DEBUG) printStackTrace()
}

fun Throwable.safeLog() {
    if (BuildConfig.DEBUG) printStackTrace()
}

fun EditText.getTextToString(): String {
    return text.toString()
}

fun EditText.str(): String {
    return text.toString()
}

fun String.getAmount(): String {
    return UtilsBase.g().getDotMoney(this)
}

fun String.getAmountVND(): String {

    return UtilsBase.g().getDotMoney(this) + " VND"
}

fun EditText.getAmountServer(): String {
    return text.toString().replace("[^\\d-.]".toRegex(), "")
}

fun String.getAmountServer(): String {
    return this.replace("[^\\d-]".toRegex(), "")
}

fun String.getAmountBig(): BigDecimal {
    return BigDecimal(this.getAmountServer())
}

fun String.html(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getString(stringResId: Int): String = resources.getString(stringResId)

/**
 * Extension method to show a keyboard for View.
 */
fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

fun JSONObject.getSafeString(key: String): String {
    if (this.has(key))
        return getString(key)
    return ""
}

fun String.getSaveJSONObject(): JSONObject? {
    return try {
        JSONObject(this)
    } catch (e: java.lang.Exception) {
        null
    }
}

/**
 * Try to hide the keyboard and returns whether it worked
 * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
 */
fun View.hideKeyboard(): Boolean {
    try {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    } catch (ignored: RuntimeException) {
    }
    return false
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        try {
            onSafeClick(it)
        } catch (e: java.lang.Exception) {
            Log.wtf("EX", e)
        }
    }
    setOnClickListener(safeClickListener)
}

class SafeClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun String.convertServerDate(): String {
    return UtilsBase.g().convertDate("dd/MM/yyyy", "ddMMyyyy", this)
}

fun String.convertSavingDate(): String {
    return UtilsBase.g().convertDate("yyyyMMdd", "dd/MM/yyyy", this)
}

fun String.convertDynamicDate(from: String, to: String): String {
    return UtilsBase.g().convertDate(from, to, this)
}

fun String.parseInt(): Int {
    return Integer.parseInt(this)
}

fun String?.removeAccentNormalize(): String {
    return UtilsBase.g().removeAccentNormalize(this ?: "")
}

fun String?.padStartExt(): String {

    return this?.let {
        if (it.length < 2) {
            it.padStart(2, '0')
        } else {
            it
        }
    } ?: ""
}


fun View.getBitmapCache(defaultColor: Int = 0x000066ad): Bitmap? {
    try {
        val bitmap =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(defaultColor)
        draw(canvas)

        return bitmap
    } catch (e: Exception) {
        e.safeLog()
    }
    return null
}

fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> {
    try {
        if (fromIndex > size) return listOf()
        if (toIndex > size) return subList(fromIndex, size)
        if (fromIndex > toIndex) return listOf()
        return subList(fromIndex, toIndex)
    } catch (e: Exception) {
        return emptyList()
    }
}

fun String.safeSubString(fromIndex: Int, toIndex: Int): String {
    try {
        val size = this.length
        if (fromIndex > size) return ""
        if (toIndex > size) return this.substring(fromIndex, size)
        if (fromIndex > toIndex) return ""
        return this.substring(fromIndex, toIndex)
    } catch (e: Exception) {
        return ""
    }
}

val Float.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()


fun String?.getTimeInMillisecond(pattern: String): Long {
    val df = SimpleDateFormat(pattern, Locale.US)
    var i: Long = -1
    try {
        val d = df.parse(this)
        i = d.time
    } catch (ex: Exception) {

    }
    return i
}

