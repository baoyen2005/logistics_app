package com.example.bettinalogistics.di

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.example.baseapp.di.Common
import com.example.bettinalogistics.ui.main.MainActivity


class AppData {
    var currentUser: String? = null
    var userId: String? = null
    var phone: String? = null
    var fullName: String? = null
    var email: String? = null
    var uri: Uri? = null

    companion object {
        private var instance: AppData? = null
        fun g(): AppData {
            if (instance == null)
                instance = AppData()
            return instance!!
        }
    }

    fun isLogined(): Boolean {
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
}