package com.icarus.unzip.base

import a.icarus.component.BaseActivity
import a.icarus.utils.WindowUtil
import android.os.Bundle
import android.os.PersistableBundle
import com.icarus.unzip.data.Event
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class Activity : BaseActivity() {
    override fun initTheme() {
        super.initTheme()
        WindowUtil.setBlackStatusText(this)
    }

    override fun setLayout(): Int {
        return USE_DATA_BINDING
    }

    companion object {
        const val BACK_TO_FRONT = "back_to_front"
        const val SELECTED_FILE_LIST = "files_selected"

        const val TARGET_FILE_PATH = "file_path"
        const val ZIP_PARA = "zip_para"
        const val UNZIP_PARA = "unzip_para"

        const val ROOT_DIR = "root_dir"
        const val INITIAL_DIR = "initial_dir"
        const val IGNORE_DIR = "ignore_dir"

        const val TITLE = "title"
        const val MEDIA_TYPE = "media_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state = State.CREATE
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        state = State.DESTROY
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        state = State.START
    }

    override fun onStop() {
        super.onStop()
        state = State.STOP
    }

    override fun onResume() {
        super.onResume()
        state = State.RESUME
    }

    override fun onPause() {
        super.onPause()
        state = State.PAUSE
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    open fun onEvent(event: Event) {
    }

    var state = State.UNKNOWN

    enum class State {
        CREATE, START, RESUME, PAUSE, STOP, DESTROY, UNKNOWN
    }

    fun isActive(): Boolean {
        return when (state) {
            State.RESUME -> true
            else -> false
        }
    }
}