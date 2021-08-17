package com.icarus.unzip.ui.activity

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.icarus.unzip.R
import com.icarus.unzip.adapter.ZipFileAdapter
import com.icarus.unzip.adapter.ZipIndexAdapter
import com.icarus.unzip.base.Activity
import com.icarus.unzip.data.FileData
import com.icarus.unzip.data.ZipFileNode
import com.icarus.unzip.databinding.ActivityZipPreviewBinding
import com.icarus.unzip.util.log
import com.icarus.unzip.util.mainThread
import com.icarus.unzip.util.subThread
import com.icarus.unzip.util.visible
import net.lingala.zip4j.ZipFile
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ZipPreviewActivity : Activity() {
    private lateinit var binding: ActivityZipPreviewBinding
    private var rootName = ""
    private var path = ""

    private val index = ArrayList<ZipFileNode>()
    private val indexAdapter = ZipIndexAdapter(index)
    private val layoutManagerIndex =
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    private val fileList = ArrayList<ZipFileNode>()
    private val fileAdapter = ZipFileAdapter(fileList)
    private val layoutManager = LinearLayoutManager(this)
    private var fileError = false
    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zip_preview)
        setBackView(R.id.back)
        binding.indexList.adapter = indexAdapter
        binding.fileList.adapter = fileAdapter
        binding.indexList.layoutManager = layoutManagerIndex
        binding.fileList.layoutManager = layoutManager
        indexAdapter.backView = binding.backFolder
    }


    override fun initData() {
        if (initFileTree()) {
            loadAdapterData()
        } else {
            fileError = true
            binding.ok.visible(false)
            binding.empty.visible(false)
            binding.error.visible(true)
        }
    }

    override fun initListener() {
        fileAdapter.itemClickListener = { item, _ ->
            if (item.isDirectory) {
                path = item.path
                loadAdapterData()
                indexAdapter.add(item)
                indexAdapter.notifyDataSetChanged()
                layoutManagerIndex.scrollToPosition(indexAdapter.itemCount - 1)
            }
        }
        fileAdapter.setOnEmptyListener {
            binding.empty.visible(it && !fileError)
        }
        indexAdapter.itemClickListener = { item, _ ->
            path = item.path
            loadAdapterData()
        }
        binding.ok.setOnClickListener {
            val intent = Intent(this, UnzipActivity::class.java)
            intent.data = getIntent().data
            startActivity(intent)
            finish()
        }
    }

    private val fileTree = ArrayList<ZipFileNode>()
    private fun initFileTree(): Boolean {
        val file = File(intent.data?.path ?: "文件不存在.zip")
        rootName = file.name.substring(0, file.name.length - 4)
        path = "$rootName/"
        indexAdapter.add(ZipFileNode(path, "", rootName, true))
        if (!file.exists()) return false
        val zipFile = ZipFile(file)
        if (!zipFile.isValidZipFile) return false
        zipFile.fileHeaders.forEach {
            val path = rootName + "/" + it.fileName
            val split = path.split(File.separator)
            var parent = ""
            var name = ""
            split.reversed().forEach { s ->
                if (name.isEmpty()) {
                    name = s
                } else {
                    parent = "$s/$parent"
                }
            }
            fileTree.add(
                ZipFileNode(
                    path,
                    parent,
                    name,
                    it.isDirectory,
                    it.uncompressedSize
                )
            )
        }
        return true
    }

    private fun loadAdapterData() {
        fileList.clear()
        fileAdapter.notifyDataSetChanged()
        subThread {
            val tag = path
            val filter = fileTree.filter { it.parent == path }.sorted()
            filter.forEach {
                if (it.isDirectory) {
                    it.size = fileTree.count { node -> node.parent == it.path }.toLong()
                }
            }
            mainThread {
                if (tag == path) {
                    fileList.clear()
                    fileList.addAll(filter)
                    fileAdapter.notifyDataSetChangedWithEmpty()
                }
            }
        }
    }
}