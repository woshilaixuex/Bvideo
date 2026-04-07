package com.elyric.common.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object BPermissionsUtils {
    // region 权限申请
    /**
     * 获取要获取的所有权限
     */
    fun buildRuntimePermissions(): List<String> {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.READ_MEDIA_VIDEO
        } else {
            permissions += Manifest.permission.READ_EXTERNAL_STORAGE
        }
        return permissions
    }

    /**
     * 获取未申请到的权限
     */
    fun getUnRequestPermissions(context: Context): List<String>{
        val deniedPermissions = buildRuntimePermissions().filterNot { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        return deniedPermissions
    }
    // endregion
}