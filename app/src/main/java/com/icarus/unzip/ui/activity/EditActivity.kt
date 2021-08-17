package com.icarus.unzip.ui.activity

import a.icarus.component.BaseDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.icarus.unzip.R
import com.icarus.unzip.base.Activity
import com.icarus.unzip.coustomView.ProgressDialog
import com.icarus.unzip.coustomView.ZipParaDialog
import com.icarus.unzip.data.Event
import com.icarus.unzip.inter.FileEditable
import com.icarus.unzip.service.FileCopyService
import com.icarus.unzip.service.FileZipService
import com.icarus.unzip.util.getNameWithoutType
import com.icarus.unzip.util.show
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
abstract class EditActivity : Activity(), FileEditable {

    @Inject
    lateinit var progressDialog: ProgressDialog
    private lateinit var launchCopyPath: ActivityResultLauncher<Intent>
    private lateinit var launchZipPath: ActivityResultLauncher<Intent>
    private val editFiles = ArrayList<String>()
    private lateinit var zipParaDialog: ZipParaDialog
    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(progressDialog)

        launchCopyPath =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != RESULT_OK) return@registerForActivityResult
                it.data?.getStringExtra(TARGET_FILE_PATH)?.run {
                    path = this
                    val intent = FileCopyService.getStartIntent(editFiles, this)
                    startService(intent)
                }
            }

        launchZipPath = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode != RESULT_OK) return@registerForActivityResult
            it.data?.getStringExtra(TARGET_FILE_PATH)?.run {
                path = this
                zipParaDialog.path = this
            }
        }
    }

    override fun copy(copyFiles: List<File>) {
        editFiles.clear()
        editFiles.addAll(copyFiles.map { it.absolutePath })

        val intent = Intent(this, DirectorySelectActivity::class.java)
        intent.putStringArrayListExtra(IGNORE_DIR, editFiles)
        intent.putExtra(TITLE, "复制到此处")
        launchCopyPath.launch(intent)
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out)
    }

    override fun zip(zipFiles: List<File>, defaultDir: File, defaultName: String?) {
        editFiles.clear()
        editFiles.addAll(zipFiles.map { it.absolutePath })

        zipParaDialog = ZipParaDialog(this)
        path = defaultDir.absolutePath
        zipParaDialog.path = defaultDir.absolutePath
        zipParaDialog.fileName = defaultName
            ?: if (zipFiles.size == 1) zipFiles[0].getNameWithoutType() else defaultDir.name
        zipParaDialog.zipStartListener = {
            val intent = FileZipService.getStartIntent(editFiles, it)
            startService(intent)
        }
        zipParaDialog.pathSelectListener = {
            val intent = Intent(this, DirectorySelectActivity::class.java)
            intent.putStringArrayListExtra(IGNORE_DIR, editFiles)
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

    override fun onEvent(event: Event) {
        when (event) {
            is Event.FileTaskSuccess -> {
                BaseDialog.Builder(this)
                    .setLayout(R.layout.dialog_to_dir)
                    .setPositiveView(R.id.confirm) {
                        finish()
                        val intent = Intent(this, FileManagerActivity::class.java)
                        intent.putExtra(INITIAL_DIR, event.path)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                    .setNegativeView(R.id.cancel)
                    .builder()
                    .show()
            }
            else -> Unit
        }
    }
}