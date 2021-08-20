package com.icarus.unzip.ui.activity

import a.icarus.component.BaseDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.icarus.unzip.R
import com.icarus.unzip.adapter.AdapterEditPlug
import com.icarus.unzip.base.Activity
import com.icarus.unzip.coustomView.FileEditMenu
import com.icarus.unzip.coustomView.ProgressDialog
import com.icarus.unzip.coustomView.ZipParaDialog
import com.icarus.unzip.data.Event
import com.icarus.unzip.enums.FileType
import com.icarus.unzip.inter.FileEditable
import com.icarus.unzip.service.FileCopyService
import com.icarus.unzip.service.FileZipService
import com.icarus.unzip.util.*
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
abstract class EditActivity : Activity(), FileEditable {

    @Inject
    lateinit var progressDialog: ProgressDialog

    val editPlug = AdapterEditPlug()

    fun initEditPlug(menu: FileEditMenu):AdapterEditPlug {
        editPlug.bindEditMenu(menu)
        menu.menuItemClickListener = editMenuItemClickListener
        return editPlug
    }

    open fun setEditMode(value: Boolean) {
        editPlug.editMode = value
    }

    private lateinit var launchCopyPath: ActivityResultLauncher<Intent>
    private lateinit var launchZipPath: ActivityResultLauncher<Intent>
    private val editFilesPath = ArrayList<String>()
    private lateinit var zipParaDialog: ZipParaDialog
    private lateinit var path: String
    private val editMenuItemClickListener = { it: FileEditMenu.MenuItem ->
        val editFiles = editPlug.getList()
        when (it) {
            FileEditMenu.MenuItem.ZIP -> zip(editFiles)
            FileEditMenu.MenuItem.UNZIP -> unzip(editFiles[0])
            FileEditMenu.MenuItem.COPY -> copy(editFiles)
            FileEditMenu.MenuItem.DELETE -> delete(editFiles)
            FileEditMenu.MenuItem.CANCEL -> Unit
        }
        setEditMode(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(progressDialog)
        launchCopyPath =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != RESULT_OK) return@registerForActivityResult
                it.data?.getStringExtra(TARGET_FILE_PATH)?.run {
                    path = this
                    val intent = FileCopyService.getStartIntent(editFilesPath, this)
                    startService(intent)
                }
            }
        launchZipPath =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != RESULT_OK) return@registerForActivityResult
                it.data?.getStringExtra(TARGET_FILE_PATH)?.run {
                    path = this
                    zipParaDialog.path = this
                }
            }
    }

    override fun copy(copyFiles: List<File>) {
        editFilesPath.clear()
        editFilesPath.addAll(copyFiles.map { it.absolutePath })

        val intent = Intent(this, DirectorySelectActivity::class.java)
        intent.putStringArrayListExtra(IGNORE_DIR, editFilesPath)
        intent.putExtra(TITLE, "复制到此处")
        launchCopyPath.launch(intent)
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out)
    }

    override fun zip(zipFiles: List<File>) {
        if (zipFiles.size == 1 && zipFiles[0].getType() == FileType.ZIP) {
            unzip(zipFiles[0])
            return
        }
        editFilesPath.clear()
        editFilesPath.addAll(zipFiles.map { it.absolutePath })

        zipParaDialog = ZipParaDialog(this)
        path = getZipPath()
        zipParaDialog.path = path
        zipParaDialog.fileName =
            if (zipFiles.size == 1) zipFiles[0].getNameWithoutType() else getZipName()
        zipParaDialog.zipStartListener = {
            val intent = FileZipService.getStartIntent(editFilesPath, it)
            startService(intent)
        }
        zipParaDialog.pathSelectListener = {
            val intent = Intent(this, DirectorySelectActivity::class.java)
            intent.putStringArrayListExtra(IGNORE_DIR, editFilesPath)
            intent.putExtra(INITIAL_DIR, zipParaDialog.path)
            intent.putExtra(TITLE, "压缩到此处")
            launchZipPath.launch(intent)
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out)
        }
        zipParaDialog.show()
    }

    override fun unzip(file: File) {
        val intent = Intent(this, UnzipActivity::class.java)
        intent.data = Uri.parse(file.absolutePath)
        startActivity(intent)
    }

    override fun delete(deleteFiles: List<File>) {
        deleteFiles.forEach { it.deleteWithDir() }
        Event.FileChanged().post()
        Event.FileDelete.post()
    }


    override fun onEvent(event: Event) {
        if (event is Event.FileTaskSuccess && isActive()) {
            BaseDialog.Builder(this)
                .setLayout(R.layout.dialog_to_dir)
                .setPositiveView(R.id.confirm) { openFileManager(event.path) }
                .setNegativeView(R.id.cancel)
                .builder()
                .show()
        }
    }

    private fun openFileManager(path: String) {
        val intent = Intent(this, FileManagerActivity::class.java)
        intent.putExtra(INITIAL_DIR, path)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        afterOpenFileManager()
    }

    abstract fun getZipPath(): String

    abstract fun getZipName(): String

    open fun afterOpenFileManager() {}
}