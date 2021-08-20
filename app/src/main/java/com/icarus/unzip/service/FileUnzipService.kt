package com.icarus.unzip.service

import android.app.IntentService
import android.content.Intent
import com.icarus.unzip.base.Activity
import com.icarus.unzip.data.Event
import com.icarus.unzip.data.UnzipPara
import com.icarus.unzip.util.log
import com.icarus.unzip.util.mainThread
import com.icarus.unzip.util.show
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.progress.ProgressMonitor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File

class FileUnzipService : IntentService(FileUnzipService::class.java.simpleName) {


    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private var running = true
    private lateinit var zipFile: ZipFile
    private lateinit var progressMonitor: ProgressMonitor

    override fun onHandleIntent(intent: Intent?) {
        val unzipPara = intent?.getSerializableExtra(Activity.UNZIP_PARA) as UnzipPara? ?: return
        Event.FileUnzipStart.post()
        running = true
        val file = File(unzipPara.file)
        zipFile = ZipFile(file)
        if (unzipPara.passWord.isNotEmpty()) {
            zipFile.setPassword(unzipPara.passWord.toCharArray())
        }
        progressMonitor = zipFile.progressMonitor
        zipFile.isRunInThread = true
        try {
            var p = -1
            zipFile.extractAll(unzipPara.targetPath)
            while (running) {
                if (progressMonitor.result == ProgressMonitor.Result.ERROR) {
                    throw ZipException("解压失败")
                }
                if (p != progressMonitor.percentDone) {
                    p = progressMonitor.percentDone
                    p.log()
                    Event.FileTaskProgress(p * 10).post()
                }
                if (p == 100) {
                    if (unzipPara.delete) {
                        file.delete()
                    }
                    running = false
                    Event.FileTaskSuccess(unzipPara.targetPath).post()
                    Event.FileTaskFinish.post()
                    Event.FileChanged().post()
                }
            }
        } catch (e: Exception) {
            mainThread {
                e.message.show()
            }
            running = false
            Event.FileTaskFailed.post()
            Event.FileTaskFinish.post()
        }
    }

    @Subscribe
    fun onEvent(event: Event.FileTaskCancel) {
        cancelTask()
    }

    private fun cancelTask() {
        if (running) {
            progressMonitor.isCancelAllTasks = true
            progressMonitor.endProgressMonitor()
        }
    }
}