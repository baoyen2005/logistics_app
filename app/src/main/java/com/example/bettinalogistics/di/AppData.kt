package com.example.bettinalogistics.di

import android.content.Intent
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.example.baseapp.di.Common
import com.example.bettinalogistics.model.CommonEntity
import com.example.bettinalogistics.model.User
import com.example.bettinalogistics.ui.activity.main.MainActivity
import com.example.bettinalogistics.utils.DataConstant
import com.example.bettinalogistics.utils.DataConstant.Companion.PRODUCT_TYPE
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_EMAIL
import com.example.bettinalogistics.utils.DataConstant.Companion.USER_ID
import com.example.bettinalogistics.utils.Utils


class AppData {
    var currentUserAuth: String? = null
    var currentUser : User? = Utils.g().getObjectFromJson(Utils.g().getDataString(DataConstant.USER)
        .toString(), User::class.java)
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
        return !TextUtils.isEmpty(g().currentUserAuth)
    }

    fun logout() {
        userId = null
        currentUserAuth = null
        val intent = Intent(Common.currentActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        Common.currentActivity!!.startActivity(intent)
        Common.currentActivity!!.overridePendingTransition(0, 0);
        ActivityCompat.finishAffinity(Common.currentActivity!!)
        Common.currentActivity!!.finish()
    }

    fun saveUser(user: User){
        Utils.g().saveDataString(USER_EMAIL, user.email)
        Utils.g().saveDataString(DataConstant.USER_ID, user.uid)
        Utils.g().saveDataString(DataConstant.USER_IMAGE, user.image)
        Utils.g().saveDataString(DataConstant.USER_PHONE, user.phone)
        Utils.g().saveDataString(DataConstant.USER_FULL_NAME, user.fullName)
        Utils.g().saveDataString(DataConstant.USER, Utils.g().getJsonFromObject(user))
    }

    fun clearOrderInfo(){
        Utils.g().saveDataString(
            DataConstant.ORDER_ADDRESS,
            null
        )
        Utils.g()
            .saveDataString(DataConstant.ORDER_TRANSPORT_TYPE, null)
        Utils.g().saveDataString(
            DataConstant.ORDER_TRANSPORT_METHOD,
            null
        )
    }
}