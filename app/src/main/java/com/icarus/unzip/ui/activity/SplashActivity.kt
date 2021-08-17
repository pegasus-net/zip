package com.icarus.unzip.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.icarus.unzip.R
import com.icarus.unzip.util.requestAllPermission
import java.util.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val code = requestAllPermission()
        if (code != 1000) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    startMainActivity()
                }
            }, 500)
        }
    }

    @Synchronized
    private fun startMainActivity() {
        if (!isFinishing) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startMainActivity()
    }
}