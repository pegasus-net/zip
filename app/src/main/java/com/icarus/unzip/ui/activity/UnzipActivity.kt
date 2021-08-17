package com.icarus.unzip.ui.activity

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.icarus.unzip.R
import com.icarus.unzip.base.Activity
import com.icarus.unzip.data.UnzipPara
import com.icarus.unzip.databinding.ActivityUnzipBinding
import com.icarus.unzip.service.FileUnzipService
import com.icarus.unzip.util.getNameWithoutType
import com.icarus.unzip.util.getTextPath
import com.icarus.unzip.util.show
import com.icarus.unzip.util.visible
import net.lingala.zip4j.ZipFile
import java.io.File

class UnzipActivity : Activity() {
    private lateinit var binding: ActivityUnzipBinding
    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_unzip)
        setBackView(R.id.back)
    }

    private lateinit var launchPath: ActivityResultLauncher<Intent>
    private var filePath = ""
        set(value) {
            field = value
            binding.targetPath.text = formatPath(value)
        }
    private var fileName = ""
    override fun initData() {
        launchPath = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode != RESULT_OK) return@registerForActivityResult
            it.data?.getStringExtra(TARGET_FILE_PATH)?.run {
                filePath = this
            }
        }
        val file = File(intent.data?.path ?: "文件不存在.zip")
        val zipFile = ZipFile(file)
        if (file.exists() && zipFile.isValidZipFile) {
            fileName = file.getNameWithoutType()
            filePath = file.parent ?: ""
            binding.passwordArea.visible(zipFile.isEncrypted)
        } else {
            finish()
            "压缩文件损坏".show()
        }
    }

    private fun formatPath(path: String): String {
        return path.getTextPath() + if (binding.singleDir.isChecked) "/${fileName}" else ""
    }

    override fun initListener() {
        binding.targetPath.setOnClickListener {
            val intent = Intent(this, DirectorySelectActivity::class.java)
            intent.putExtra(INITIAL_DIR, filePath)
            intent.putExtra(TITLE, "解压到此处")
            launchPath.launch(intent)
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out)
        }
        binding.singleDir.setOnCheckedChangeListener { _, _ ->
            binding.targetPath.text = formatPath(filePath)
        }
        binding.ok.setOnClickListener {
            val intent = Intent(this,FileUnzipService::class.java)
            val file = File(filePath + if (binding.singleDir.isChecked) "/${fileName}" else "")
            if (file.exists() && !file.isDirectory) {
                "存在同名文件，无法创建文件夹".show()
                return@setOnClickListener
            }
            file.mkdirs()
            val unzipPara = UnzipPara(
                getIntent().data?.path ?: "",
                file.absolutePath,
                binding.password.text.toString(),
                binding.allowDelete.isChecked
            )
            intent.putExtra(UNZIP_PARA, unzipPara)
            startService(intent)
            finish()
        }
    }
}