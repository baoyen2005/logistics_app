package com.example.bettinalogistics.utils

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.baseapp.di.Common
import com.example.baseapp.view.safeLog
import com.example.bettinalogistics.utils.AppConstant.Companion.KEY_SEARCH_HISTORY
import com.example.bettinalogistics.utils.AppConstant.Companion.TAG
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.text.ParseException
import java.text.SimpleDateFormat
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
    fun getFullNameShort(value: String): String? {
        var data = ""
        if (data.length >= 13) {
            data = value.substring(0, 13)+ "...";
        } else {
            data = value;
        }
        return data
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
            Log.d(TAG, "getObjectFromJson: $e")
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

    fun convertDate(fromFormat: String?, toFormat: String?, strDate: String): String {
        try {
            val simpleDateFormat = SimpleDateFormat(fromFormat)
            val simpleTo = SimpleDateFormat(toFormat)
            return simpleTo.format(simpleDateFormat.parse(strDate))
        } catch (ex: ParseException) {
        }
        return strDate
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
    // lưu dữ liệu trong SharedPreferences
    fun saveDataString(key: String, value: String?){
        val sharedPreference =  Common.currentActivity?.getSharedPreferences("PREFERENCE_LOGISTICS_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference?.edit()
        editor?.putString(key,value)
        editor?.apply()
    }

    fun saveDataLong(key: String, value: Long){
        val sharedPreference =  Common.currentActivity?.getSharedPreferences("PREFERENCE_LOGISTICS_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference?.edit()
        editor?.putLong(key,value)
        editor?.apply()
    }

    fun saveDataBoolean(key: String, value: Boolean){
        val sharedPreference =  Common.currentActivity?.getSharedPreferences("PREFERENCE_LOGISTICS_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference?.edit()
        editor?.putBoolean(key,value)
        editor?.apply()
    }

    fun saveDataInt(key: String, value: Int){
        val sharedPreference =  Common.currentActivity?.getSharedPreferences("PREFERENCE_LOGISTICS_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference?.edit()
        editor?.putInt(key,value)
        editor?.apply()
    }

    fun getDataString(key: String) : String?{
        val sharedPreference =  Common.currentActivity?.getSharedPreferences("PREFERENCE_LOGISTICS_NAME", Context.MODE_PRIVATE)
        return  sharedPreference?.getString(key, null)
    }

    fun getDataBoolean(key: String) : Boolean?{
        val sharedPreference =  Common.currentActivity?.getSharedPreferences("PREFERENCE_LOGISTICS_NAME", Context.MODE_PRIVATE)
        return  sharedPreference?.getBoolean(key,false)
    }

    fun getDataLong(key: String) : Long?{
        val sharedPreference =  Common.currentActivity?.getSharedPreferences("PREFERENCE_LOGISTICS_NAME", Context.MODE_PRIVATE)
        return  sharedPreference?.getLong(key,-1L)
    }

    fun getDataInt(key: String) : Int?{
        val sharedPreference =  Common.currentActivity?.getSharedPreferences("PREFERENCE_LOGISTICS_NAME", Context.MODE_PRIVATE)
        return  sharedPreference?.getInt(key,-1)
    }

    fun clearData(key: String){
        val myPrefs =  Common.currentActivity?.getSharedPreferences("PREFERENCE_LOGISTICS_NAME", Context.MODE_PRIVATE)
        myPrefs?.edit()?.remove(key)?.apply()
    }

    fun setSearchHistory(type: Int, searchKey: String, isDelete: Boolean = false) {
        val searchKeysStorage =
            Utils.g().provideGson()
                .fromJson(getDataString(KEY_SEARCH_HISTORY), object :
                    TypeToken<HashMap<Int, MutableList<String>>>() {}.type)?:  HashMap<Int, MutableList<String>>()
        if (isDelete) {
            searchKeysStorage[type]?.remove(searchKey)
        } else {
            if (searchKeysStorage.containsKey(type)) {
                if (searchKeysStorage[type]?.contains(searchKey) == true) {
                    searchKeysStorage[type]?.remove(searchKey)
                }
                searchKeysStorage[type]?.add(0,searchKey)
            } else {
                searchKeysStorage[type] = mutableListOf(searchKey)
            }
        }
        saveDataString(KEY_SEARCH_HISTORY, g().provideGson().toJson(searchKeysStorage))
    }

    fun getSearchKeyInStorage(): Map<Int, MutableList<String>> {
        return provideGson().fromJson(
                    getDataString(KEY_SEARCH_HISTORY), object :
                        TypeToken<HashMap<Int, MutableList<String>>>() {}.type)?: HashMap<Int, MutableList<String>>()
    }

    fun clearHistory(type:Int){
        val searchKeysStorage =  provideGson().fromJson(
            getDataString(KEY_SEARCH_HISTORY), object :
                TypeToken<HashMap<Int, MutableList<String>>>() {}.type)?: HashMap<Int, MutableList<String>>()
        searchKeysStorage[type]?.clear()
        saveDataString(KEY_SEARCH_HISTORY, provideGson().toJson(searchKeysStorage))
    }

    fun send(data: String?) {
        try {
            FirebaseCrashlytics.getInstance().log(data ?: "")
        } catch (e: Exception) {
            e.safeLog()
        }
    }
}