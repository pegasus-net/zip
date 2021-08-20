package com.icarus.unzip.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.icarus.unzip.R
import com.icarus.unzip.adapter.FileAdapter
import com.icarus.unzip.data.Event
import com.icarus.unzip.databinding.ActivitySearchBinding
import com.icarus.unzip.impl.FileNameCompare
import com.icarus.unzip.util.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : EditActivity() {

    companion object {
        fun start(activity: Activity, root: String? = null) {
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra("root", root)
            activity.startActivity(intent)
        }
    }

    private lateinit var binding: ActivitySearchBinding
    private val list = ArrayList<File>()
    private val rootList = ArrayList<File>()
    private lateinit var mAdapter: FileAdapter
    private var rootFile = getRootFile()
    private val layoutManager = LinearLayoutManager(this)
    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        setBackView(R.id.back)
    }

    private var root = getRootFile()
    override fun initData() {
        initEditPlug(binding.fileEditMenu)
        mAdapter = FileAdapter(list, editPlug)
        editPlug.editStateChangedListener = { mAdapter.notifyDataSetChanged() }
        binding.list.adapter = mAdapter
        binding.list.layoutManager = layoutManager
        val rootPath = intent.getStringExtra("root") ?: ""
        if (File(rootPath).exists()) {
            root = File(rootPath)
        }
        initRootList()
    }


    override fun initListener() {
        binding.searchKey.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                subThread { loadDataToAdapter() }
            }
            false
        }
    }

    override fun getZipPath(): String {
        return root.absolutePath
    }

    override fun getZipName(): String {
        return root.name
    }

    @Synchronized
    private fun initRootList() {
        subThread {
            val scanResult = ArrayList<File>()
            root.scan(scanResult, false)
            rootList.clear()
            rootList.addAll(scanResult)
            loadDataToAdapter()
        }
    }

    @Synchronized
    private fun loadDataToAdapter() {

        val key = binding.searchKey.text.toString()
        val filter = if (key.isNotEmpty()) rootList.toList().filter { it.name.contains(key) }
            .sortedWith(FileNameCompare()) else ArrayList()
        mainThread {
            binding.empty.visible(filter.isEmpty() && key.isNotEmpty())
            list.clear()
            list.addAll(filter)
            setEditMode(false)
        }

    }

    override fun onEvent(event: Event) {
        super.onEvent(event)
        if (event is Event.FileTaskSuccess) {
            initRootList()
        }
    }

    override fun delete(deleteFiles: List<File>) {
        super.delete(deleteFiles)
        rootList.removeAll(deleteFiles)
        loadDataToAdapter()
    }

    override fun onBackPressed() {
        if (editPlug.editMode) {
            editPlug.editMode = false
            return
        }
        super.onBackPressed()
    }

    override fun afterOpenFileManager() {
        super.afterOpenFileManager()
        finish()
    }
}