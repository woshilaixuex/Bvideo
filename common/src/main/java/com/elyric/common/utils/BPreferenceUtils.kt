package com.elyric.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class BPreferenceUtils(context: Context){
    private var context: Context = context.applicationContext
    private var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)

    // region 用户协议

    val isAcceptServiceAgreement: Boolean
        get() = preferences.getBoolean(ACCEPT_SERVICE,false)
    fun setAcceptServiceAgreement(){
        preferences.edit().putBoolean(ACCEPT_SERVICE,true).apply()
    }
    // endregion

    companion object {
        private val ACCEPT_SERVICE = "ACCEPT_SERVICE"
        private var instance: BPreferenceUtils? = null
        @Synchronized
        fun getInstance(context: Context): BPreferenceUtils{
            if (null == instance) {
                instance = BPreferenceUtils(context)
            }
            return instance!!
        }
    }
}