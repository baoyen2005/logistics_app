package com.example.bettinalogistics.utils

import android.text.TextUtils
import android.util.Patterns
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.regex.Pattern

class Utils private constructor() {
    companion object {
        const val EN_LANG = "EN"
        const val VN_LANG = "VI"
        private var instance: Utils? = null

        @JvmStatic
        fun g(): Utils {
            if (instance == null) instance = Utils()
            return instance!!
        }
    }

    private var gson: Gson? = null

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

    fun provideGson(): Gson {
        if (gson == null) gson = GsonBuilder().disableHtmlEscaping().create()
        return gson!!
    }

    //Convert data json save
    fun <T> getObjectFromJson(json: String, classOfT: Class<T>?): T? {
        return try {
            provideGson().fromJson(json, classOfT)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> getJsonFromObject(ob: T): String? {
        return try {
            provideGson().toJson(ob)
        } catch (e: Exception) {
            null
        }
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
}