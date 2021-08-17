package com.icarus.unzip.base

import a.icarus.component.MonitorApplication
import a.icarus.utils.Logger
import android.app.Activity
import android.content.Intent
import com.icarus.unzip.ad.Constants
import com.icarus.unzip.ui.activity.SplashActivity
import dagger.hilt.android.HiltAndroidApp
import org.litepal.LitePal

@HiltAndroidApp
class App : MonitorApplication() {
    override fun init() {
        LitePal.initialize(this)
        Logger.setType(Logger.ERROR)
    }

    override fun onAppBackgroundToFront(activity: Activity) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra(Constants.AdState.BACK_TO_FRONT, true)
        activity.startActivity(intent)
    }

    override fun onAppFrontIgnore(activity: Activity): Boolean {
        return true
    }
}