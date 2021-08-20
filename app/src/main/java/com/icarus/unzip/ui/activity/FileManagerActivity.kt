package com.icarus.unzip.ui.activity

import android.content.Intent
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.icarus.unzip.R
import com.icarus.unzip.adapter.FileAdapter
import com.icarus.unzip.data.Event
import com.icarus.unzip.databinding.ActivityFileBinding
import com.icarus.unzip.impl.FileNameCompare
import com.icarus.unzip.impl.FileSizeCompare
import com.icarus.unzip.impl.FileTimeCompare
import com.icarus.unzip.util.*
import java.io.File

class FileManagerActivity : EditActivity() {

    private lateinit var binding: ActivityFileBinding
    private val list = ArrayList<File>()
    private lateinit var mAdapter: FileAdapter
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
        setBackView(R.id.back)
    }

    override fun initData() {
        initEditPlug(binding.fileEditMenu)
        mAdapter = FileAdapter(list, editPlug)
        editPlug.editStateChangedListener = { mAdapter.notifyDataSetChanged() }
        binding.list.adapter = mAdapter
        binding.list.layoutManager = layoutManager

        parent = intent.getStringExtra(INITIAL_DIR)?.let { File(it) } ?: rootFile
        loadData()
    }

    override fun initListener() {

        mAdapter.itemClickListener = { file, _ ->
            if (file.isDirectory) {
                savePosition(parent.absolutePath)
                parent = file
                loadData()
            } else {
                file.open()
            }
        }

        mAdapter.setOnEmptyListener {
            binding.empty.visibility = if (it) View.VISIBLE else View.GONE
        }

        binding.menu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popup_0 -> comparator = FileNameCompare()
                    R.id.popup_1 -> comparator = FileSizeCompare()
                    R.id.popup_2 -> comparator = FileTimeCompare()
                    R.id.popup_3 -> setEditMode(true)
                    R.id.popup_4 -> refresh()
                }
                true
            }
            popupMenu.show()
        }
    }

    private var comparator: Comparator<File> = FileNameCompare()
        set(value) {
            if (field.javaClass != value.javaClass) {
                field = value
                list.sortWith(value)
                mAdapter.notifyDataSetChanged()
            }
        }

    private fun loadData(reset: Boolean = false) {
        list.clear()
        mAdapter.notifyDataSetChanged()
        binding.title.text = if (parent == rootFile) "内部储存" else parent.name
        subThread {
            val tag = parent
            val data = parent.listFiles()
                ?.filter { !it.isNeedHide() }
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
            editPlug.editMode -> {
                setEditMode(false)
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

    override fun getZipPath(): String {
        return parent.absolutePath
    }

    override fun getZipName(): String {
        return parent.name
    }

    private fun refresh() {
        subThread {
            val tag = parent
            val data = parent.listFiles()
                ?.filter { !it.isNeedHide() }
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