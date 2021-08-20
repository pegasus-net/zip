@file:JvmName("Tool")

package com.icarus.unzip.util

import a.icarus.utils.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.greenrobot.eventbus.EventBus
import java.util.*

fun getAppContext(): Context = Icarus.getContext()
fun getAppResources(): Resources = getAppContext().resources

fun Int.toPx() =
    ConversionTool.dp2px(this.toFloat())

fun Int.toPxF() =
    ConversionTool.dp2pxF(this.toFloat())

fun Float.toPx() =
    ConversionTool.dp2px(this)

fun Float.toPxF() =
    ConversionTool.dp2pxF(this)


fun mainThread(run: () -> Unit) {
    ThreadManager.runOnUiThread(run)
}

fun subThread(run: () -> Unit) {
    ThreadManager.runOnThreadPool(run)
}

fun <T> T.log() {
    Logger.t(this)
}

fun <T> T.print() {
    println(this)
}

fun <T> T.show() {
    ToastUtil.show(this.toString())
}

fun time() {
    Logger.t(System.currentTimeMillis())
}


fun Activity.requestAllPermission(): Int {
    try {
        val packageInfo: PackageInfo =
            packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        val requestedPermissions = packageInfo.requestedPermissions

        val mPermissionList = ArrayList<String>()
        requestedPermissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(it)
            }
        }
        val permissionArray = mPermissionList.toTypedArray()

        if (mPermissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionArray, 1000)
            return 1000
        }
        return 0

    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return -1
    }
}

fun Fragment.startActivity(clazz: Class<out Activity>) {
    val intent = Intent(this.context, clazz)
    startActivity(intent)
}

fun Activity.startActivity(clazz: Class<out Activity>) {
    val intent = Intent(this, clazz)
    startActivity(intent)
}

fun View.visible(b: Boolean) {
    visibility = if (b) View.VISIBLE else View.GONE
}

fun <T> T.registerEvent() {
   EventBus.getDefault().register(this)
}
fun <T> T.unregisterEvent() {
   EventBus.getDefault().unregister(this)
}


