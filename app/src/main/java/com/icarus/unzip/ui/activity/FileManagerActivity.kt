package com.icarus.unzip.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.icarus.unzip.R
import com.icarus.unzip.adapter.FileAdapter
import com.icarus.unzip.coustomView.ZipParaDialog
import com.icarus.unzip.data.Event
import com.icarus.unzip.data.FileData
import com.icarus.unzip.databinding.ActivityFileBinding
import com.icarus.unzip.enums.FileType
import com.icarus.unzip.impl.FileNameCompare
import com.icarus.unzip.impl.FileSizeCompare
import com.icarus.unzip.impl.FileTimeCompare
import com.icarus.unzip.model.ActivityFileViewModel
import com.icarus.unzip.service.FileCopyService
import com.icarus.unzip.util.*
import java.io.File

class FileManagerActivity : EditActivity() {

    private lateinit var binding: ActivityFileBinding
    private val model = ActivityFileViewModel()
    private val list = ArrayList<FileData>()
    private val mAdapter = FileAdapter(list)
    private var rootFile = getRootFile()
    private var parent = rootFile
    private val layoutManager = LinearLayoutManager(this)

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        rootFile = getRootFile()
        parent = intent?.getStringExtra(INITIAL_DIR)?.let { File(it) } ?: rootFile
        loadData()
    }

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_file)
        binding.model = model
        setBackView(R.id.back)
    }

    override fun initData() {
        mAdapter.setOnEmptyListener {
            binding.empty.visibility = if (it) View.VISIBLE else View.GONE
        }
        mAdapter.filesChangeListener = {
            model.operationAllow.set(it.isNotEmpty())
            if (it.size == 1 && it[0].getType() == FileType.ZIP) {
                model.unzip.set("解压")
            } else {
                model.unzip.set("压缩")
            }
        }
        binding.list.adapter = mAdapter
        binding.list.layoutManager = layoutManager
        parent = intent.getStringExtra(INITIAL_DIR)?.let { File(it) } ?: rootFile
        loadData()
    }

    override fun initListener() {
        mAdapter.itemClickListener = { item, _ ->
            if (item.file.isDirectory) {
                savePosition(parent.absolutePath)
                parent = item.file
                loadData()
            } else {
                item.file.open()
            }
        }
        mAdapter.editModeChangeListener = {
            binding.menuCard.visible(it)
        }

        binding.zip.setOnClickListener {
            val editFiles = mAdapter.editFiles
            if (editFiles.isEmpty()) {
                return@setOnClickListener
            }
            if (editFiles.size == 1 && editFiles[0].getType() == FileType.ZIP) {
                unzip(editFiles[0])
            } else {
                zip(mAdapter.editFiles, parent)
            }
            mAdapter.editMode = false
        }


        binding.copy.setOnClickListener {
            val editFiles = mAdapter.editFiles
            if (editFiles.isEmpty()) {
                return@setOnClickListener
            }
            copy(editFiles)
            mAdapter.editMode = false
        }

        binding.delete.setOnClickListener {
            mAdapter.delete()
        }
        binding.cancel.setOnClickListener {
            mAdapter.editMode = false
        }

        binding.menu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popup_0 -> comparator = FileNameCompare()
                    R.id.popup_1 -> comparator = FileSizeCompare()
                    R.id.popup_2 -> comparator = FileTimeCompare()
                    R.id.popup_3 -> mAdapter.editMode = true
                    R.id.popup_4 -> refresh()
                }
                true
            }
            popupMenu.show()
        }
    }

    private var comparator: Comparator<FileData> = FileNameCompare()
        set(value) {
            if (field.javaClass != value.javaClass) {
                value.log()
                field = value
                list.sortWith(value)
                mAdapter.notifyDataSetChanged()
            }
        }

    private fun loadData(reset: Boolean = false) {
        list.clear()
        mAdapter.notifyDataSetChanged()
        model.title.set(if (parent == rootFile) "内部储存" else parent.name)
        subThread {
            val tag = parent
            val data = parent.listFiles()
                ?.filter { !it.isHidden }
                ?.map { FileData(it) }
                ?.sortedWith(comparator)
                ?: ArrayList()
            mainThread {
                if (tag == parent) {
                    list.clear()
                    list.addAll(data)
                    mAdapter.notifyDataSetChangedWithEmpty()
                    if (reset) {
                        resetPosition(parent.absolutePath)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        when {
            mAdapter.editMode -> {
                mAdapter.editMode = false
            }
            parent == rootFile -> {
                super.onBackPressed()
            }
            else -> {
                parent = parent.parentFile ?: rootFile
                loadData(true)
            }
        }
    }

    private val positionMap = HashMap<String, Position>()
    private fun resetPosition(key: String) {
        positionMap[key]?.run {
            layoutManager.scrollToPositionWithOffset(first, top)
        }
    }

    private fun savePosition(key: String) {
        val first = layoutManager.findFirstCompletelyVisibleItemPosition()
        val top = layoutManager.findViewByPosition(first)?.top ?: 0
        positionMap[key] = Position(first, top)
    }

    data class Position(val first: Int, val top: Int)

    override fun onEvent(event: Event) {
        super.onEvent(event)
        when (event) {
            is Event.FileChanged -> if (event.file == null || event.file.parentFile == parent) refresh()
            else -> Unit
        }
    }

    private fun refresh() {
        subThread {
            val tag = parent
            val data = parent.listFiles()
                ?.filter { !it.isHidden }
                ?.map { FileData(it) }
                ?.sortedWith(comparator)
                ?: ArrayList()
            mainThread {
                if (tag == parent) {
                    list.clear()
                    list.addAll(data)
                    mAdapter.notifyDataSetChangedWithEmpty()
                }
            }
        }
    }
}