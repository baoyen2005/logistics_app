package com.vnpay.merchant.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.Build.VERSION
import android.os.Vibrator
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.baseapp.di.Common
import com.example.baseapp.view.safeLog
import java.io.*
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.roundToInt


class UtilsBase private constructor() {
    companion object {
        const val EN_LANG = "EN"
        const val VN_LANG = "VI"
        private var instance: UtilsBase? = null

        @JvmStatic
        fun g(): UtilsBase {
            if (instance == null) instance = UtilsBase()
            return instance!!
        }
    }

    val CURRENCY = "VND"
    val TAG = "TAG"
    private var widthScreen = 0
    private var heightScreen = 0

    fun changeToForiegnLanguage(ctx: Context, isForiegn: Boolean) {
        val locale = Locale(if (isForiegn) "en" else "vi_VN")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        ctx.resources.updateConfiguration(config, ctx.resources.displayMetrics)
    }

    fun getDPtoPX(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }

    fun getDPtoPX(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Common.currentActivity!!.resources.displayMetrics
        ).toInt()
    }

    fun getSPtoPX(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            dp.toFloat(),
            Common.currentActivity!!.resources.displayMetrics
        ).toInt()
    }

    fun getDotMoneyHasCcy(str: String, ccy: String): String {
        return if (!TextUtils.isEmpty(ccy)) getDotMoney(str, ".") + " " + ccy else getDotMoney(
            str, "."
        )
    }

    fun getDotMoney(str: String?): String {
        return getDotMoney(str, ".") // sẽ dùng . phân cách hàng thập phân
    }

    fun getDotMoney(str: String?, dot: String): String {
        val str = str ?: ""
        var rs = str
        try {
            if (str.endsWith(".00")) {
                rs = str.replace(".00", "")
            }
            val splitCcy: String
            val temp: Array<String>
            if ("." == dot) {
                splitCcy = ","
                temp = rs.split(",".toRegex()).toTypedArray()
            } else {
                splitCcy = "."
                temp = rs.split("\\.".toRegex()).toTypedArray()
            }
            return if (temp.size < 2) {
                rs.replace("[^\\d-]".toRegex(), "")
                    .replace("\\B(?=(\\d{3})+(?!\\d))".toRegex(), dot)
            } else {
                temp[0].replace("[^\\d-]".toRegex(), "")
                    .replace("\\B(?=(\\d{3})+(?!\\d))".toRegex(), dot) + splitCcy + temp[1]
            }
        } catch (e: java.lang.Exception) {
        }
        return rs
    }

    fun Captain(str: String): String {
        try {
            return str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1, str.length)
        } catch (e: Exception) {
        }
        return str
    }

    fun getTimeIntDate(dateTime: String?, formatDate: String?): Long {
        try {
            val s = SimpleDateFormat(formatDate, Locale.US)
            val c = s.parse(dateTime)
            return c.time
        } catch (e: Exception) {
        }
        return 0
    }

    fun Vibrate() {
        try {
            val v = Common.currentActivity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(50)
        } catch (e: Exception) {
        }
    }

    fun isMaxRangeTime(fromDate: String?, toDate: String?, i: Int, pattern: String?): Boolean {
        return getDateTime(toDate, pattern) - getDateTime(fromDate, pattern) > i * 86400
    }

    fun getDateTime(time: String?, pattern: String?): Long {
        val df = SimpleDateFormat(pattern, Locale.US)
        var i: Long = -1
        try {
            val d = df.parse(time)
            i = d.time / 1000
        } catch (ex: Exception) {
            //ex.printStackTrace();
        }
        return i
    }

    val toDay: String
        get() {
            val calendar = Calendar.getInstance()
            return getTypeDate(
                calendar[Calendar.DATE], calendar[Calendar.MONTH] + 1, calendar[Calendar.YEAR], "/"
            )
        }

    fun getTypeDate(d: Int, m: Int, y: Int, pattern: String): String {
        return (if (d < 10) "0$d" else d).toString() + "" + pattern + "" + (if (m < 10) "0$m" else m) + "" + pattern + "" + getBeautyNumber(
            y
        )
    }

    fun getTypeDate(c: Calendar, pattern: String): String {
        return getBeautyNumber(c[Calendar.DATE]) + "" + pattern + "" + getBeautyNumber(c[Calendar.MONTH] + 1) + "" + pattern + "" + getBeautyNumber(
            c[Calendar.YEAR]
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(date: String, format: String): Date {
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.parse(date)!!
    }

    fun getBeautyNumber(size: Int): String {
        return if (size == 0 || size > 9) "" + size else "0$size"
    }

    fun getBeautyNumberCountOTT(count: Int): String {
        return if (count > 9) "+9" else count.toString()
    }

    val monthAgo: String
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -30)
            return getTypeDate(
                calendar[Calendar.DATE], calendar[Calendar.MONTH] + 1, calendar[Calendar.YEAR], "/"
            )
        }

    val weekAgo: String
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -7)
            return getTypeDate(
                calendar[Calendar.DATE], calendar[Calendar.MONTH] + 1, calendar[Calendar.YEAR], "/"
            )
        }

    fun parseDate(date: String?): IntArray? {
        try {
            val r =
                Pattern.compile("([0]*[1-9]|[12][0-9]|3[01])[- /.]([0]*[1-9]|1[012])[- /.]([\\d]{4})")
            val m = r.matcher(date)
            if (m.find()) {
                return intArrayOf(m.group(1).toInt(), m.group(2).toInt(), m.group(3).toInt())
            }
        } catch (e: Exception) {
        }
        return null
    }

    fun convertDate(fromFormat: String?, toFormat: String?, strDate: String): String {
        try {
            val simpleDateFormat = SimpleDateFormat(fromFormat)
            val simpleTo = SimpleDateFormat(toFormat)
            return simpleTo.format(simpleDateFormat.parse(strDate))
        } catch (ex: ParseException) {
        }
        return strDate
    }

    fun removeAccentNormalize(str: String?): String {
        var str = str
        if (str.isNullOrEmpty()) return ""
        str = str.lowercase(Locale.getDefault())
        str = str.replace("[àáạảãâầấậẩẫăằắặẳẵ]".toRegex(), "a")
        str = str.replace("[éèẽẹêẻềếệểễ]".toRegex(), "e")
        str = str.replace("[ìíịỉĩ]".toRegex(), "i")
        str = str.replace("[òóọỏõôồốộổỗơờớợởỡ]".toRegex(), "o")
        str = str.replace("[ùúụủũưừứựửữ]".toRegex(), "u")
        str = str.replace("[ỳýỵỷỹ]".toRegex(), "y")
        str = str.replace("đ", "d")
        return str
    }

    private var keys: HashMap<String, String?>? = null
    private var hiphopPattern: Pattern? = null
    fun removeAccent(str: String): String {
        var str = str
        if (TextUtils.isEmpty(str)) return ""
        str = str.replace("Đ", "D")
        str = str.replace("[ỲÝỴỶỸ]".toRegex(), "Y")
        str = str.replace("[ÙÚỤỦŨƯỪỨỰỬỮ]".toRegex(), "U")
        str = str.replace("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]".toRegex(), "O")
        str = str.replace("[ÌÍỊỈĨ]".toRegex(), "I")
        str = str.replace("[ÈÉẸẺẼÊỀẾỆỂỄ]".toRegex(), "E")
        str = str.replace("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]".toRegex(), "A")
        str = str.replace("[àáạảãâầấậẩẫăằắặẳẵ]".toRegex(), "a")
        str = str.replace("[éèẽẹêẻềếệểễ]".toRegex(), "e")
        str = str.replace("[ìíịỉĩ]".toRegex(), "i")
        str = str.replace("[òóọỏõôồốộổỗơờớợởỡ]".toRegex(), "o")
        str = str.replace("[ùúụủũưừứựửữ]".toRegex(), "u")
        str = str.replace("[ỳýỵỷỹ]".toRegex(), "y")
        str = str.replace("đ", "d")
        return str
    }

    fun removeAccentHipHop(str: String): String? {
        genKeys()
        if (hiphopPattern == null) {
            val DATE_PATTERN = "([^(a-zA-Z|\\s)|@#$%^&*!\\/\\\\])"
            hiphopPattern = Pattern.compile(DATE_PATTERN)
        }
        val m = hiphopPattern!!.matcher(str)
        var lastKey = ""
        while (m.find()) {
            lastKey = m.group()
        }
        return if (!lastKey.isEmpty() && keys!!.containsKey(lastKey)) {
            keys!![lastKey]
        } else " $str"
    }

    private fun genKeys() {
        if (keys == null) {
            keys = HashMap()
            keys!!["Đ"] = "D"
            keys!!["Ê"] = "E"
            keys!!["É"] = "S"
            keys!!["È"] = "F"
            keys!!["Ẹ"] = "J"
            keys!!["Ẻ"] = "R"
            keys!!["Ẽ"] = "X"
            keys!!["Ý"] = "S"
            keys!!["Ỳ"] = "F"
            keys!!["Ỵ"] = "J"
            keys!!["Ỷ"] = "R"
            keys!!["Ỹ"] = "X"
            keys!!["Ư"] = "W"
            keys!!["Ú"] = "S"
            keys!!["Ù"] = "F"
            keys!!["Ụ"] = "J"
            keys!!["Ủ"] = "R"
            keys!!["Ũ"] = "X"
            keys!!["Ô"] = "O"
            keys!!["Ơ"] = "W"
            keys!!["Ó"] = "S"
            keys!!["Ò"] = "F"
            keys!!["Ọ"] = "J"
            keys!!["Ỏ"] = "R"
            keys!!["Õ"] = "X"
            keys!!["ƯƠ"] = "W"
            keys!!["Â"] = "A"
            keys!!["Á"] = "S"
            keys!!["À"] = "F"
            keys!!["Ạ"] = "J"
            keys!!["Ả"] = "R"
            keys!!["Ã"] = "X"
            keys!!["Í"] = "S"
            keys!!["Ì"] = "F"
            keys!!["Ị"] = "J"
            keys!!["Ỉ"] = "R"
            keys!!["Ĩ"] = "X"
            keys!!["ê"] = "e"
            keys!!["é"] = "s"
            keys!!["è"] = "f"
            keys!!["ẹ"] = "j"
            keys!!["ẻ"] = "r"
            keys!!["ẽ"] = "x"
            keys!!["ý"] = "s"
            keys!!["ỳ"] = "f"
            keys!!["ỵ"] = "j"
            keys!!["ỷ"] = "r"
            keys!!["ỹ"] = "x"
            keys!!["ư"] = "w"
            keys!!["ú"] = "s"
            keys!!["ù"] = "f"
            keys!!["ụ"] = "j"
            keys!!["ủ"] = "r"
            keys!!["ũ"] = "x"
            keys!!["ô"] = "o"
            keys!!["ơ"] = "w"
            keys!!["ó"] = "s"
            keys!!["ò"] = "f"
            keys!!["ọ"] = "j"
            keys!!["ỏ"] = "r"
            keys!!["õ"] = "x"
            keys!!["ươ"] = "w"
            keys!!["â"] = "a"
            keys!!["ă"] = "w"
            keys!!["á"] = "s"
            keys!!["à"] = "f"
            keys!!["ạ"] = "j"
            keys!!["ả"] = "r"
            keys!!["ã"] = "x"
            keys!!["í"] = "s"
            keys!!["ì"] = "f"
            keys!!["ị"] = "j"
            keys!!["ỉ"] = "r"
            keys!!["ĩ"] = "x"
            keys!!["đ"] = "d"
        }
    }

    val today: String
        get() {
            val c = Calendar.getInstance()
            return getBeautyNumber(c[Calendar.DAY_OF_MONTH]) + "/" + getBeautyNumber(c[Calendar.MONTH] + 1) + "/" + c[Calendar.YEAR]
        }

    fun writeFile(fileName: String?, bm: Bitmap) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(fileName)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: Exception) {
            Log.wtf("EXC", e)
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
            }
        }
    }

    val currentDateTime2: String
        get() {
            val c = Calendar.getInstance()
            return getBeautyNumber(c[Calendar.DAY_OF_MONTH]) + "/" + getBeautyNumber(c[Calendar.MONTH] + 1) + "/" + c[Calendar.YEAR] + " " + getBeautyNumber(
                c[Calendar.HOUR_OF_DAY]
            ) + ":" + getBeautyNumber(c[Calendar.MINUTE]) + ":" + getBeautyNumber(
                c[Calendar.SECOND]
            )

        }

    fun convertToLong(value: String?, default: Long = 0L): Long {
        return try {
            value.toString().toLong()
        } catch (e: ParseException) {
            default
        }
    }

    fun requestPermission(context: FragmentActivity?, Permission: String, requestCode: Int) {
        Log.wtf(TAG, "Permission has NOT been granted. Requesting permission.")
        if (ActivityCompat.shouldShowRequestPermissionRationale(context!!, Permission)) {
            Log.wtf(TAG, "Permission rationale to provide additional context.")
            ActivityCompat.requestPermissions(context, arrayOf(Permission), requestCode)
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(Permission), requestCode)
        }
    }

    fun formatDotMoney(value: String?, num: Int): String {
        return try {
            var f: DecimalFormat? = null
            f = when (num) {
                0 ->                     // case 2:
                    DecimalFormat("###,###")
                1 -> DecimalFormat("###,##0.0")
                2 -> DecimalFormat("###,##0.00")
                3 -> DecimalFormat("###,##0.000")
                else -> DecimalFormat("###,###")
            }
            val format = NumberFormat.getInstance(Locale.CHINESE)
            val d: Double = format.parse(value).toDouble()
            var s = f.format(d)
            val index1 = s.indexOf(".")
            val index2 = s.indexOf(",")
            if (index1 > index2) {
                s = s.replace(',', 'a')
                s = s.replace('.', ',')
                s = s.replace('a', '.')
            }
            s
        } catch (e: Exception) {
            ""
        }
    }

    fun convertInt(input: String): Int {
        var input = input
        input = input.replace("[^\\d-]".toRegex(), "")
        return if (TextUtils.isEmpty(input)) {
            0
        } else input.toInt()
    }

    val aWeek: String
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 7)
            return getTypeDate(
                calendar[Calendar.DATE], calendar[Calendar.MONTH] + 1, calendar[Calendar.YEAR], "/"
            )
        }

    fun hiddenNumber(phone: String, startLength: Int, endLength: Int): String {
        try {
            val lengh = phone.length
            val start = StringBuilder(phone.substring(0, startLength))
            for (i in 0 until lengh - startLength - endLength) {
                start.append("*")
            }
            start.append(phone.substring(phone.length - endLength, phone.length))
            return start.toString()
        } catch (e: Exception) {
            Log.wtf("EX", e)
        }
        return phone
    }

    private var FONT_BOLD: Typeface? = null
    private var FONT_ITALIC: Typeface? = null
    private var FONT_NORMAL: Typeface? = null
    private var FONT_SEMI: Typeface? = null
    private var FONT_MEDIUM: Typeface? = null
    private var FONT_REGULAR: Typeface? = null
    private var FONT_BLACK: Typeface? = null

    fun getFont(type: Int, context: Context): Typeface? {
        return when (type) {
            1 -> {
                if (FONT_MEDIUM == null) {
                    FONT_MEDIUM = Typeface.createFromAsset(context.assets, "fonts/ssp_regular.otf")
                }
                FONT_MEDIUM
            }
            2 -> {
                if (FONT_BOLD == null) {
                    FONT_BOLD = Typeface.createFromAsset(context.assets, "fonts/ssp_bold.otf")
                }
                FONT_BOLD
            }
            3 -> {
                if (FONT_ITALIC == null) {
                    FONT_ITALIC =
                        Typeface.createFromAsset(context.assets, "fonts/ssp_regular.otf")
                }
                FONT_ITALIC
            }
            else -> {
                if (FONT_REGULAR == null) {
                    FONT_REGULAR = Typeface.createFromAsset(context.assets, "fonts/ssp_regular.otf")
                }
                FONT_REGULAR
            }
        }
    }

    fun isValidPhone(phoneValue: String): Boolean {
        val p = Pattern.compile("^(0)+[\\d]{9}$")
        val m = p.matcher(phoneValue)
        return m.matches()
    }

    fun circle(bitmapimg: Bitmap, maxSize: Int): Bitmap? {
        var output = Bitmap.createBitmap(bitmapimg.width, bitmapimg.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmapimg.width, bitmapimg.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawCircle(bitmapimg.width / 2f, bitmapimg.height / 2f, bitmapimg.width / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmapimg, rect, rect, paint)
        if (maxSize >= 0 && output.width > maxSize) output = Bitmap.createScaledBitmap(
            output, maxSize, maxSize, true
        )
        return output
    }

    fun getScreenWidth(): Int {
        genScreenSize()
        return widthScreen
    }

    private fun genScreenSize() {
        try {
            val displayMetrics = Resources.getSystem().displayMetrics
            heightScreen = displayMetrics.heightPixels
            widthScreen = displayMetrics.widthPixels
        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        }
    }

    fun getScreenHeight(): Int {
        genScreenSize()
        return heightScreen
    }

    fun writeFile(file: File?, bm: Bitmap) {
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 75, out)
        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                Log.wtf("EXX", e)
            }
        }
    }

    fun getBitmapFromFile(f: File?): Bitmap? {
        try {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_4444
            return BitmapFactory.decodeStream(FileInputStream(f), null, options)
        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        }
        return null
    }

    fun getNavigationBarHeight(): Int {
        try {
            val metrics = DisplayMetrics()
            Common.currentActivity!!.windowManager.defaultDisplay.getMetrics(metrics)
            val usableHeight = metrics.heightPixels
            if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Common.currentActivity!!.windowManager.defaultDisplay.getRealMetrics(metrics)
            }
            val realHeight = metrics.heightPixels
            if (realHeight > usableHeight) return realHeight - usableHeight
        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        }
        return 0
    }

    fun getAssets(filePath: String?): InputStream? {
        try {
            val assetManager = Common.currentActivity!!.assets
            val istr: InputStream
            istr = assetManager.open(filePath!!)
            return istr
        } catch (e: java.lang.Exception) {
        }
        return null
    }

    fun hash(data: String): String? {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(data.toByteArray(charset("UTF-8")))
            val digest = md.digest()
            val hexString = StringBuilder()
            for (aDigest in digest) {
                val hex = Integer.toHexString(0xff and aDigest.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex.uppercase(Locale.getDefault()))
            }
            return try {
                UUID.nameUUIDFromBytes(hexString.toString().toByteArray())
                    .toString()
            } catch (e: java.lang.Exception) {
                hexString.toString()
            }
        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        }
        return data
    }

    fun hash256(data: String): String? {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(data.toByteArray(charset("UTF-8")))
            val digest = md.digest()
            val hexString = StringBuilder()
            for (aDigest in digest) {
                val hex = Integer.toHexString(0xff and aDigest.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex.uppercase(Locale.getDefault()))
            }
            return hexString.toString()
        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        }
        return data
    }

    fun getOTP(otp: EditText, message: String) {
        try {
            val r = Pattern.compile("[\\s]+([\\d]{6})[\\s.,]+")
            val m = r.matcher("$message.")
            if (m.find()) {
                otp.setText(m.group(1))
            }
        } catch (e: java.lang.Exception) {
            e.safeLog()
        }
    }

    fun getBeautyPhone(phone: String): String {
        if (TextUtils.isEmpty(phone)) return phone
        var phone = phone
        if (phone.startsWith("84")) phone = "+$phone"
        phone = phone.replace("+84", "0")
        return phone.take(10)
    }

    //Ẩn list các view
    fun setGoneViews(vararg views: View) {
        views.forEach {
            if (it.visibility != View.GONE) it.visibility = View.GONE
        }
    }

    //Hiện list các view
    fun setVisibleViews(vararg views: View) {
        views.forEach {
            if (it.visibility != View.VISIBLE) it.visibility = View.VISIBLE
        }
    }

    //Hiện list các view
    fun setInvisibleViews(vararg views: View) {
        views.forEach {
            if (it.visibility != View.INVISIBLE) it.visibility = View.INVISIBLE
        }
    }

    //Validate tên quá dài
    fun getFullNameShort(fullName: String): String? {
        if (TextUtils.isEmpty(fullName)) return fullName
        var fullNameShort = ""
        val fullNameArr = fullName.split(" ").toTypedArray()
        if (fullNameArr.size >= 5) {
            for (i in fullNameArr.size - 4 until fullNameArr.size) {
                fullNameShort += "${fullNameArr[i]} "
            }
        } else {
            fullNameShort = fullName
        }
        return fullNameShort
    }

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun verifyPassword(pass: String): Boolean {
        return Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,50}\$").matcher(pass)
            .matches()
    }

    fun verifyLengthPass(pass: String): Boolean {
        return pass.length in 8..50
    }

    fun getImageDrawable(context: Context, id: Int): Drawable {
        return AppCompatResources.getDrawable(context, id)!!
    }

    //Kiểm tra đã cài passcode chưa
    fun isDeviceScreenLocked(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isDeviceLocked(context)
        } else {
            isPatternSet(context) || isPassOrPinSet(context)
        }
    }

    fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions.isNotEmpty()) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * @return true if pass or pin or pattern locks screen
     */
    @TargetApi(23)
    private fun isDeviceLocked(context: Context): Boolean {
        val keyguardManager: KeyguardManager =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager // api 23+
        return keyguardManager.isDeviceSecure
    }

    /**
     * @return true if pattern set, false if not (or if an issue when checking)
     */
    private fun isPatternSet(context: Context): Boolean {
        val cr: ContentResolver = context.contentResolver
        return try {
            val lockPatternEnable: Int =
                Settings.Secure.getInt(cr, Settings.Secure.LOCK_PATTERN_ENABLED)
            lockPatternEnable == 1
        } catch (e: Settings.SettingNotFoundException) {
            false
        }
    }

    /**
     * @return true if pass or pin set
     */
    private fun isPassOrPinSet(context: Context): Boolean {
        val keyguardManager: KeyguardManager =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager // api 16+
        return keyguardManager.isKeyguardSecure
    }

    //Mask phone number *
    fun maskPhoneNumber(phone: String): String {
        return phone.replace("\\B(\\d{2})\\d+(\\d{3})".toRegex(), "$1****$2")
    }

    fun copyToClipboard(context: Context, value: String?) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text", value)
        clipboard.setPrimaryClip(clip)
    }

    fun replaceNumCard(s: String): String {
        return s.replace("X", "*").replace("x", "*")
    }

    fun createShapeDrawable(
        context: Context,
        shapeColor: Int,
        radius: Float,
        width: Int,
        height: Int
    ): Drawable {
        val var5 = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        return ShapeDrawable(RoundRectShape(var5, null, null)).apply {
            intrinsicWidth = width.toPx(context)
            intrinsicHeight = height.toPx(context)
            paint.color = shapeColor
        }
    }

    fun Int.toPx(context: Context) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).roundToInt()


    fun validateMail(emailStr: String?): Boolean {
        val validEmailRegex =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
        val matcher = validEmailRegex.matcher(emailStr)
        return matcher.find()
    }
}