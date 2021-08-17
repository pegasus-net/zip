package com.icarus.unzip.service

import a.icarus.utils.FileUtil
import android.app.IntentService
import android.content.Intent
import com.icarus.unzip.base.Activity
import com.icarus.unzip.data.Event
import com.icarus.unzip.data.UnzipPara
import com.icarus.unzip.util.log
import com.icarus.unzip.util.show
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import net.lingala.zip4j.model.enums.EncryptionMethod
import net.lingala.zip4j.progress.ProgressMonitor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class FileUnzipService : IntentService(FileUnzipService::class.java.simpleName) {


    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private var currentSize = 0L
    private var totalSize = 0L
    private var finishSize = 0L
    private var progress = 0
    private var running = true
    private lateinit var zipFile: ZipFile
    private lateinit var progressMonitor: ProgressMonitor

    override fun onHandleIntent(intent: Intent?) {
        val unzipPara = intent?.getSerializableExtra(Activity.UNZIP_PARA) as UnzipPara? ?: return
        EventBus.getDefault().post(Event.FileUnzipStart)
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
                    throw ZipException("密码错误")
                }
                if (p != progressMonitor.percentDone) {
                    p = progressMonitor.percentDone
                    EventBus.getDefault().post(Event.FileTaskProgress(p * 10))
                }
                if (p == 100) {
                    if (unzipPara.delete) {
                        file.delete()
                    }
                    running = false
                    EventBus.getDefault().post(Event.FileTaskSuccess(unzipPara.targetPath))
                    EventBus.getDefault().post(Event.FileTaskFinish)
                    EventBus.getDefault().post(Event.FileChanged(null))
                }
            }
        } catch (e: Exception) {
            e.message.show()
            running = false
            EventBus.getDefault().post(Event.FileTaskFailed)
            EventBus.getDefault().post(Event.FileTaskFinish)
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