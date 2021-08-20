package com.icarus.unzip.service

import a.icarus.utils.FileUtil
import a.icarus.utils.Recycle
import android.app.IntentService
import android.content.Intent
import com.icarus.unzip.ad.Constants
import com.icarus.unzip.base.Activity
import com.icarus.unzip.data.Event
import com.icarus.unzip.util.getAppContext
import com.icarus.unzip.util.mainThread
import com.icarus.unzip.util.show
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class FileCopyService : IntentService(FileCopyService::class.java.simpleName) {
    companion object {

        fun getStartIntent(list: ArrayList<String>?, path: String?): Intent {
            val intent = Intent(getAppContext(), FileCopyService::class.java)
            intent.putExtra(Activity.SELECTED_FILE_LIST, list)
            intent.putExtra(Activity.TARGET_FILE_PATH, path)
            return intent
        }
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private var total = 0L
    private var current = 0L
    private var progress = 0
    private var running = false

    override fun onHandleIntent(intent: Intent?) {
        running = true
        val files =
            (intent?.getStringArrayListExtra(Activity.SELECTED_FILE_LIST))
        val map = files?.map { File(it) } ?: ArrayList()
        val path = intent?.getStringExtra(Activity.TARGET_FILE_PATH)
        if (map.isEmpty() || path == null) return
        Event.FileCopyStart.post()
        map.forEach {
            total += FileUtil.getSize(it)
        }
        current = 0L
        map.forEach {
            it.copy(path)
        }
        Event.FileTaskFinish.post()
        if (running) {
            running = false
            Event.FileTaskSuccess(path).post()
        }

    }

    @Subscribe
    fun onEvent(event: Event.FileTaskCancel) {
        cancelTask()
    }

    private fun cancelTask() {
        if (running) {
            running = false
        }
    }

    @Synchronized
    private fun File.copy(path: String) {
        if (!running) return
        val dir = File(path)
        dir.mkdirs()
        if (dir.exists() && dir.isDirectory) {
            var file = File(dir, name)
            if (isDirectory) {
                file.mkdirs()
                listFiles()?.forEach {
                    it.copy(file.absolutePath)
                }
            } else {
                while (file.exists()) {
                    var name = file.name
                    val lastIndexOf = name.lastIndexOf(".")
                    name = if (lastIndexOf != -1) {
                        name.substring(0, lastIndexOf) + "_副本" + name.substring(lastIndexOf)
                    } else {
                        "${name}_副本"
                    }
                    file = File(dir, name)
                }
                var fis: FileInputStream? = null
                var fos: FileOutputStream? = null
                try {
                    fis = FileInputStream(this)
                    fos = FileOutputStream(file)
                    var len: Int
                    val buffer = ByteArray(1024 * 8)
                    while (fis.read(buffer).also { len = it } != -1 && running) {
                        fos.write(buffer, 0, len)
                        current += len
                        val p = (current * 1000 / total).toInt()
                        if (progress != p) {
                            progress = p
                            Event.FileTaskProgress(progress).post()
                        }
                    }
                    if (!running) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    mainThread {
                        e.message.show()
                    }
                    file.delete()
                    Event.FileTaskFailed.post()
                    cancelTask()
                } finally {
                    Event.FileChanged(file).post()
                    Recycle.close(fis)
                    Recycle.close(fos)
                }
            }
        }
    }
}