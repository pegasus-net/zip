package com.icarus.unzip.ui.activity

import a.icarus.component.BaseDialog
import a.icarus.utils.WindowUtil
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.icarus.unzip.R
import com.icarus.unzip.adapter.DirectoryAdapter
import com.icarus.unzip.adapter.FileIndexAdapter
import com.icarus.unzip.base.Activity
import com.icarus.unzip.data.Event
import com.icarus.unzip.databinding.ActivityDirectoryBinding
import com.icarus.unzip.util.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class DirectorySelectActivity : Activity() {
    private lateinit var binding: ActivityDirectoryBinding

    private lateinit var files: List<File>
    private var rootFile = Environment.getExternalStorageDirectory()
    private var parent = rootFile
    private var title = "选择文件夹"
    private val index = ArrayList<File>()
    private val indexAdapter = FileIndexAdapter(index)
    private val layoutManagerIndex =
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    private val directoryList = ArrayList<File>()
    private val directoryAdapter = DirectoryAdapter(directoryList)
    private val layoutManager = LinearLayoutManager(this)

    override fun initView() {
        WindowUtil.setWhiteStatusText(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_directory)
        setBackView(R.id.back)
        binding.indexList.adapter = indexAdapter
        binding.dirList.adapter = directoryAdapter
        binding.indexList.layoutManager = layoutManagerIndex
        binding.dirList.layoutManager = layoutManager
    }

    override fun initData() {

        val list = intent.getStringArrayListExtra(IGNORE_DIR)
        files = list?.map { File(it) } ?: ArrayList()
        directoryAdapter.ignore = files

        parent = intent.getStringExtra(INITIAL_DIR)?.let {
            val file = File(it)
            if (file.exists()) file else null
        } ?: rootFile

        intent.getStringExtra(TITLE)?.let {
            title = it
        }
        var temp = parent
        while (temp != rootFile && temp != null) {
            index.add(temp)
            temp = temp.parentFile
        }
        index.add(rootFile)
        index.reverse()
        indexAdapter.backView = binding.backFolder

        binding.ok.text = title
        loadData()
    }

    override fun initListener() {
        directoryAdapter.itemClickListener = { dir, _ ->
            parent = dir
            indexAdapter.add(dir)
            layoutManagerIndex.scrollToPosition(index.size - 1)
            loadData()
        }
        indexAdapter.itemClickListener = { file, _ ->
            parent = file
            loadData()
        }
        binding.ok.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(TARGET_FILE_PATH, parent.absolutePath)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        binding.newFolder.setOnClickListener {
            val dialog = BaseDialog.Builder(this)
                .setLayout(R.layout.dialog_new_folder)
                .setNegativeView(R.id.cancel)
                .builder()
            dialog.setViewOnClickListener(R.id.confirm) {
                val text = dialog.findViewById<EditText>(R.id.file_name).text.toString().trim()
                if (newFolder(text)) dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun loadData() {
        directoryList.clear()
        directoryAdapter.notifyDataSetChanged()
        subThread {
            val tag = parent
            val data = parent.listFiles()
                ?.filter { !it.isNeedHide() && it.isDirectory }
                ?.sortedBy {
                    it.name.toLowerCase(Locale.ROOT)
                } ?: ArrayList()
            synchronized(this) {
                mainThread {
                    if (tag == parent) {
                        directoryList.addAll(data)
                        directoryAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun newFolder(name: String): Boolean {
        if (name.isEmpty()) {
            fileNameEmpty()
            return false
        }
        val file = File(parent, name)
        if (!file.exists()) {
            if (file.mkdir()) {
                if (!directoryList.contains(file)) {
                    directoryList.add(file)
                    Event.FileChanged(file).post()
                    directoryAdapter.notifyDataSetChanged()
                    layoutManager.scrollToPosition(directoryList.size - 1)
                    return true
                }

            } else {
                fileNameInvalid()
            }
        } else {
            fileExists()
        }
        return false
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out)
        binding.root.background = ColorDrawable(Color.TRANSPARENT)
    }
}