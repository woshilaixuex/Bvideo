package com.elyric.bredio.view.component.splashPage

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.elyric.bredio.MainActivity
import com.elyric.bredio.R
import com.elyric.common.utils.BPermissionsUtils
import com.elyric.common.utils.BPreferenceUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            goHome()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (!BPreferenceUtils.getInstance(this).isAcceptServiceAgreement) {
            showServiceAgreementDialog()
        } else {
            requestPermissionsAfterAgreement()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun goHome() {
        lifecycleScope.launch {
            delay(2000)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun showServiceAgreementDialog() {
        ServiceAgreeDialogFragment.show(supportFragmentManager) {
            BPreferenceUtils.getInstance(this).setAcceptServiceAgreement()
            requestPermissionsAfterAgreement()
        }
    }

    private fun requestPermissionsAfterAgreement() {
        val deniedPermissions = BPermissionsUtils.getUnRequestPermissions(this)
        if (deniedPermissions.isEmpty()) {
            goHome()
            return
        }
        permissionLauncher.launch(deniedPermissions.toTypedArray())
    }



}

