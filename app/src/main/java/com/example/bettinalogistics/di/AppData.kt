package com.example.bettinalogistics.di

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.example.baseapp.di.Common
import com.example.bettinalogistics.ui.activity.main.MainActivity
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_EMAIL
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ID
import com.example.bettinalogistics.utils.Utils


class AppData {
    var currentUser: String? = null
    var userId: String? = Utils.g().getDataString(USER_ID)

    companion object {
        private var instance: AppData? = null
        fun g(): AppData {
            if (instance == null)
                instance = AppData()
            return instance!!
        }
    }

    fun isSignUp(): String? {
        return Utils.g().getDataString(USER_EMAIL)
    }

    fun isLogin(): Boolean {
        return !TextUtils.isEmpty(g().currentUser)
    }

    fun logout() {
        userId = null
        currentUser = null
        val intent = Intent(Common.currentActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        Common.currentActivity!!.startActivity(intent)
        Common.currentActivity!!.overridePendingTransition(0, 0);
        ActivityCompat.finishAffinity(Common.currentActivity!!)
        Common.currentActivity!!.finish()
    }

    fun saveUser(email : String?, uri :Uri?, phone : String?, fullName:String?, uid: String?){
        Utils.g().saveDataString(USER_EMAIL, email)
        Utils.g().saveDataString(DataConstant.USER_ID, uid)
        Utils.g().saveDataString(DataConstant.USER_IMAGE, uri.toString())
        Utils.g().saveDataString(DataConstant.USER_PHONE, phone)
        Utils.g().saveDataString(DataConstant.USER_FULL_NAME, fullName)
    }
}