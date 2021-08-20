package com.icarus.unzip.data

import org.greenrobot.eventbus.EventBus
import java.io.File

sealed class Event {


    object FileTaskCancel : Event()
    object FileTaskFailed : Event()
    class FileTaskSuccess(val path: String) : Event()
    object FileTaskFinish : Event()
    class FileTaskProgress(val progress: Int) : Event()

    object FileCopyStart : Event()
    object FileZipStart : Event()
    object FileUnzipStart : Event()


    class FileChanged(val file: File? = null) : Event()
    object FileDelete : Event()

    fun post() {
        EventBus.getDefault().post(this)
    }
}

