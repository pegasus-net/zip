package com.icarus.unzip.service

import a.icarus.utils.FileUtil
import android.app.IntentService
import android.content.Intent
import com.icarus.unzip.base.Activity
import com.icarus.unzip.data.Event
import com.icarus.unzip.data.ZipPara
import com.icarus.unzip.util.*
import net.lingala.zip4j.ZipFile
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FileZipService : IntentService(FileZipService::class.java.simpleName) {

    companion object {

        fun getStartIntent(list: ArrayList<String>?, para: ZipPara?): Intent {
            val intent = Intent(getAppContext(), FileZipService::class.java)
            intent.putExtra(Activity.SELECTED_FILE_LIST, list)
            intent.putExtra(Activity.ZIP_PARA, para)
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

    private var currentSize = 0L
    private var totalSize = 0L
    private var finishSize = 0L
    private var progress = 0
    private var running = true
    private lateinit var zipFile: ZipFile
    private lateinit var progressMonitor: ProgressMonitor

    override fun onHandleIntent(intent: Intent?) {

        val fileList =
            intent?.getStringArrayListExtra(Activity.SELECTED_FILE_LIST)
        val map = fileList?.map { File(it) } ?: ArrayList()
        val zipPara = intent?.getSerializableExtra(Activity.ZIP_PARA) as ZipPara?
        if (map.isEmpty() || zipPara == null) return
        EventBus.getDefault().post(Event.FileZipStart)
        running = true
        val file = File(zipPara.filePath, zipPara.fileName)
        if (file.exists()) {
            file.delete()
        }
        zipFile = ZipFile(file)
        progressMonitor = zipFile.progressMonitor
        val para = ZipParameters()
        when (zipPara.zipLevel) {
            0 -> para.compressionLevel = CompressionLevel.FASTEST
            1 -> para.compressionLevel = CompressionLevel.FAST
            2 -> para.compressionLevel = CompressionLevel.NORMAL
            3 -> para.compressionLevel = CompressionLevel.MAXIMUM
            4 -> para.compressionLevel = CompressionLevel.ULTRA
            else -> para.compressionLevel = CompressionLevel.NORMAL
        }

        if (zipPara.lockType != 0 && zipPara.passWord.isNotEmpty()) {
            zipFile.setPassword(zipPara.passWord.toCharArray())
            para.isEncryptFiles = true
            para.encryptionMethod = EncryptionMethod.AES
            when (zipPara.lockType) {
                1 -> para.aesKeyStrength = AesKeyStrength.KEY_STRENGTH_128
                2 -> para.aesKeyStrength = AesKeyStrength.KEY_STRENGTH_192
                3 -> para.aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
                else -> para.aesKeyStrength = AesKeyStrength.KEY_STRENGTH_256
            }
        }
        val sizeMap = HashMap<File, Long>()
        val fileStack = Stack<File>()
        zipFile.isRunInThread = true
        map.forEach {
            sizeMap[it] = FileUtil.getSize(it)
            totalSize += sizeMap[it] ?: 0
            fileStack.push(it)
        }
        para.compressionMethod = CompressionMethod.DEFLATE
        para.compressionLevel = CompressionLevel.NORMAL
        try {
            while (running) {
                if (fileStack.isNotEmpty() && progressMonitor.state == ProgressMonitor.State.READY) {
                    finishSize += currentSize
                    val pop = fileStack.pop()
                    currentSize = sizeMap[pop] ?: 0
                    if (pop.isFile) {
                        zipFile.addFile(pop, para)
                    }
                    if (pop.isDirectory) {
                        zipFile.addFolder(pop, para)
                    }
                }
                val p = if (totalSize != 0L) {
                    ((finishSize * 100 + currentSize * progressMonitor.percentDone) * 10 / totalSize).toInt()
                } else {
                    1000
                }
                if (progress != p) {
                    progress = p
                    EventBus.getDefault().post(Event.FileTaskProgress(progress))
                }
                if (p == 1000) {
                    running = false
                    if (zipPara.delete) {
                        mainThread { "删除源文件".show() }
                        map.forEach { it.deleteWithDir() }
                    }
                    EventBus.getDefault().post(Event.FileTaskSuccess(zipPara.filePath))
                    EventBus.getDefault().post(Event.FileTaskFinish)
                    EventBus.getDefault().post(Event.FileChanged(file))
                }
            }
        } catch (e: Exception) {
            running = false
            zipFile.file.delete()
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
            running = false
            progressMonitor.isCancelAllTasks = true
            zipFile.file.delete()
        }
    }
}