package com.example.baseapp.utils

import android.annotation.TargetApi
import android.app.Activity
import android.app.KeyguardManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.ParcelFileDescriptor
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.*
import android.view.View
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.baseapp.R
import com.example.baseapp.captures.MoneyReader
import com.example.baseapp.di.Common
import com.example.baseapp.view.safeLog
import java.io.*
import java.math.BigDecimal
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


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

    fun changeToForeignLanguage(ctx: Context, isForeign: Boolean) {
        val locale = Locale(if (isForeign) "en" else "vi_VN")
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

    fun getBeautyNumber(size: Int): String {
        return if (size == 0 || size > 9) "" + size else "0$size"
    }

    fun getBeautyNumberCountOTT(count: Int): String {
        return if (count > 9) "9+" else count.toString()
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

    fun hiddenStartContent(content: String, storeLastLength: Int): String {
        try {
            val start = StringBuilder()
            for (i in 0 until content.length - storeLastLength) {
                start.append("*")
            }
            start.append(content.takeLast(storeLastLength))
            return start.toString()
        } catch (e: Exception) {
            Log.wtf("EX", e)
        }
        return content
    }

    fun getBitmapFromDrawable(context: Context?, drawable: Drawable): Bitmap? {
        try {
            return if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
        } catch (e: Exception) {
            Log.wtf("EXX", e)
        }
        return null
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
                    FONT_MEDIUM = Typeface.createFromAsset(context.assets, "fonts/sf_medium.otf")
                }
                FONT_MEDIUM
            }
            2 -> {
                if (FONT_BOLD == null) {
                    FONT_BOLD = Typeface.createFromAsset(context.assets, "fonts/sf_bold.otf")
                }
                FONT_BOLD
            }
            3 -> {
                if (FONT_ITALIC == null) {
                    FONT_ITALIC =
                        Typeface.createFromAsset(context.assets, "fonts/sf_lightitalic.otf")
                }
                FONT_ITALIC
            }
            else -> {
                if (FONT_REGULAR == null) {
                    FONT_REGULAR = Typeface.createFromAsset(context.assets, "fonts/sf_regular.otf")
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


    fun getBitmapFromAsset(context: Context, filePath: String?, size: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            var size = UtilsBase.g().getDPtoPX(size)
            val assetManager = context.assets
            val istr: InputStream
            istr = assetManager.open(filePath!!)
            bitmap = BitmapFactory.decodeStream(istr)
            bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false)
        } catch (e: java.lang.Exception) {
        }
        return bitmap
    }

    fun getDrawableFromBitMap(res: Resources?, bm: Bitmap?): Drawable? {
        return BitmapDrawable(res, bm)
    }

    fun checkAmountInvalid(amountValue: String?, balanceValue: String?): Boolean {
        try {
            val amount = BigDecimal(amountValue?.replace("[^\\d\\-.]".toRegex(), ""))
            val balance = BigDecimal(balanceValue?.replace("[^\\d\\-.]".toRegex(), ""))
            if (balance < amount) {
                return true
            }
        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        }
        return false
    }

    fun getMoneyReader(resources: Resources, amount: String): String {
        val moneyReader = MoneyReader(resources)
        return moneyReader.convert(amount)
    }


    fun getBitmapFromMediaStore(context: Context, pathImage: String?, size: Int): Bitmap? {
        try {
            val bm =
                MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(pathImage))
            return circle(bm, size)
        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        }
        return null
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

    fun Capital(str: String): String? {
        return try {
            str.substring(0, 1).uppercase(Locale.getDefault())
        } catch (var2: java.lang.Exception) {
            str
        }
    }

    fun getBitmapFromPath(uri: Uri): Bitmap? {
        val path = uri.toString()
        if (path.startsWith("content://com.google.android.apps.photos.content")) {
            try {
                val inputStream: InputStream? =
                    Common.currentActivity!!.contentResolver.openInputStream(
                        uri
                    )
                if (inputStream != null) {
                    return BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: java.lang.Exception) {
                Log.wtf("EXX", e)
                return getBitmapFromUri(uri)
            }
        } else {
            return getBitmapFromUri(uri)
        }
        return null
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? =
                Common.currentActivity!!.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, o)
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            val sizeRequire = 1080
            while (true) {
                if (width_tmp / 2 < sizeRequire || height_tmp / 2 < sizeRequire) break
                width_tmp /= 2
                height_tmp /= 2
                scale++
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            o2.inPreferredConfig = Bitmap.Config.ARGB_4444
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, o2)
            parcelFileDescriptor?.close()
            return image
        } catch (e: java.lang.Exception) {
            Log.wtf("EXX", e)
        }
        return null
    }

    fun getScreenWidth(): Int {
        genScreenSize()
        return widthScreen
    }

    fun getScreenSize(): String {
        try {
            val displayMetrics = Resources.getSystem().displayMetrics
            heightScreen = displayMetrics.heightPixels
            widthScreen = displayMetrics.widthPixels
            return "$widthScreen*$heightScreen"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0x0"
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

    fun calculateHeightImageBackground(): Int {
        var height = (getScreenWidth() / 0.75).toInt()
        if (getScreenHeight() * 0.9 < height) {
            height = (getScreenWidth() * 0.75).toInt()
        }
        return height
    }

    fun chosseImageFromGallery(context: Activity, tag: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        context.startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            tag
        )
    }

    fun takePhoto(context: Activity, tag: Int) {
        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val imageFileName = "ewallet_temps${System.currentTimeMillis()}.jpg"
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, imageFileName)
            val outputUri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            context.startActivityForResult(
                cameraIntent,
                tag
            )
        } catch (e: Exception) {
            Log.wtf("EXX", e)
        }
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

    fun convertWelcomeHour(context: Context): String {
        val c = Calendar.getInstance()
        return when (c[Calendar.HOUR_OF_DAY]) {
            in 7..9 -> context.getString(R.string.txt_chao_buoi_sang)
            in 10..12 -> context.getString(R.string.txt_chao_buoi_trua)
            in 13..18 -> context.getString(R.string.txt_chao_buoi_chieu)
            in 19..23 -> context.getString(R.string.txt_chao_buoi_toi)
            else -> context.getString(R.string.txt_chao_ngay_moi)
        }

    }
    fun getMonthYear(data: String): String {
        return try {
            val formatDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val cal: Calendar = Calendar.getInstance()
            cal.time = formatDate.parse(data)

            var stringData = (cal[Calendar.MONTH] + 1).toString()
            stringData = if (stringData.length == 1) {
                "0$stringData" + "/" + cal[Calendar.YEAR]
            } else {
                stringData + "/" + cal[Calendar.YEAR]
            }
            stringData
        } catch (ex: java.lang.Exception) {
            ""
        }
    }

    fun openBrowser(url: String, context: Context){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    fun getBitmapFromPath(uri: Uri?, context: Context?): Bitmap? {
        val path = uri.toString()
        if (path.startsWith("content://com.google.android.apps.photos.content")) {
            try {
                val `is` = context!!.contentResolver.openInputStream(uri!!)
                if (`is` != null) {
                    return BitmapFactory.decodeStream(`is`)
                }
            } catch (e: Exception) {
                return getBitmapFromUri(context, uri)
            }
        } else {
            return getBitmapFromUri(context, uri)
        }
        return null
    }


    private fun getBitmapFromUri(context: Context?, uri: Uri?): Bitmap? {
        try {
            val parcelFileDescriptor =
                uri?.let { context?.contentResolver?.openFileDescriptor(it, "r") }
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
            return image
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
    fun getFileImageEKYC(context: Context, fileName: String?): File? {
        return fileName?.let {
            File(File(context.filesDir, "ekycs").apply {
                if (!exists())
                    mkdirs()
            }, fileName)
        } ?: kotlin.run {
            null
        }
    }

    fun settingPermissionNotify(packageName: String, uid: Int): Intent {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", packageName)
        intent.putExtra("app_uid", uid)
        intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
        return intent
    }

    fun getIdYoutubeFromUrl(data: String): String {
        val splits = data.split("^http.{0,12}youtu\\.{0,1}be(\\.com){0,1}\\/(watch|user|v|embed){0,1}\\/{0,1}\\?{0,1}(v=){0,1}".toRegex())
            .toTypedArray()
        if (splits.size > 1) {
            return splits[1].take(11)
        }
        return ""
    }

    fun checkOverDate(checkTime: Calendar?, timeMax: Calendar?): Boolean {
        if (checkTime == null || timeMax==null) {
            return false
        }
        if (checkTime.get(Calendar.YEAR) > timeMax.get(Calendar.YEAR)) {
            return true
        } else if (checkTime.get(Calendar.YEAR) == timeMax.get(Calendar.YEAR)) {
            if (checkTime.get(Calendar.MONTH) > timeMax.get(Calendar.MONTH)) {
                return true
            } else if (checkTime.get(Calendar.MONTH) == timeMax.get(Calendar.MONTH)) {
                return checkTime.get(Calendar.DAY_OF_MONTH) > timeMax.get(Calendar.DAY_OF_MONTH)
            }
            return false
        }
        return false
    }
}