package com.elyric.common.utils

import android.os.Process

object BProcessUtils {
    /**
     * 杀死当前App进程
     */
    fun killApp(){
        Process.killProcess(Process.myPid())
    }
}