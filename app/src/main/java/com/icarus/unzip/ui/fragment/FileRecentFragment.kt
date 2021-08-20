package com.icarus.unzip.ui.fragment

import a.icarus.component.BaseFragment
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.icarus.unzip.R
import com.icarus.unzip.adapter.AdapterEditPlug
import com.icarus.unzip.adapter.RecentFileAdapter
import com.icarus.unzip.data.Event
import com.icarus.unzip.data.ExpandableTitle
import com.icarus.unzip.databinding.FragmentFileRecentBinding
import com.icarus.unzip.enums.FileType
import com.icarus.unzip.ui.activity.EditActivity
import com.icarus.unzip.util.*
import okhttp3.internal.ignoreIoExceptions
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FileRecentFragment : BaseFragment() {
    private lateinit var binding: FragmentFileRecentBinding

    private val scanResult = ArrayList<File>()
    private val indexKey = ArrayList<ExpandableTitle>()
    private val fileMap = HashMap<ExpandableTitle, ArrayList<File>>()
    private val helper = SecondaryListAdapterHelper(indexKey, fileMap, 2)
    private lateinit var mAdapter: RecentFileAdapter
    private lateinit var editPlug: AdapterEditPlug
    override fun setLayout(): Int {
        return R.layout.fragment_file_recent
    }

    override fun initView(view: View) {
        binding = DataBindingUtil.bind(view)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterEvent()
    }

    override fun initData() {
        editPlug = (mActivity as EditActivity).initEditPlug(binding.fileEditMenu)
        mAdapter = RecentFileAdapter(helper, editPlug)
        editPlug.editStateChangedListener = { mAdapter.notifyDataSetChanged() }
        binding.mainList.adapter = mAdapter
        binding.mainList.layoutManager = LinearLayoutManager(mActivity)
        binding.refresh.setColorSchemeColors(ContextCompat.getColor(mContext, R.color.main_color))
        refresh()
    }

    override fun initListener() {
        binding.refresh.setOnRefreshListener {
            refresh()
        }
    }

    private fun refresh(anim: Boolean = true) {
        if (anim) {
            binding.refresh.isRefreshing = true
        }
        subThread {
            scanFile()
            loadDataToAdapter()
            mainThread {
                binding.refresh.isRefreshing = false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        editPlug.editMode = false
    }

    @Synchronized
    private fun scanFile() {
        scanResult.clear()
        val fileList = ArrayList<File>()
        getRootFile().scan(fileList)
        scanResult.addAll(fileList.filter { it.getType() != FileType.UNKNOWN })
    }

    @Synchronized
    private fun loadDataToAdapter() {
        val list = scanResult.toList()
        val pictureDirMap = HashMap<ExpandableTitle, ArrayList<File>>()
        list.forEach {
            val expandableTitle =
                ExpandableTitle(it.getLastTime(), it.getLastTime().msg, it.getLastTime().id)
            val index = expandableTitle.takeUnless { indexKey.contains(expandableTitle) }
                ?: indexKey[indexKey.indexOf(expandableTitle)]
            val data = pictureDirMap.getOrPut(index) { ArrayList() }
            data.add(it)
        }
        val keys = pictureDirMap.map { entry ->
            entry.value.sortBy { -it.lastModified() }
            entry.key
        }
        mainThread {
            binding.empty.visible(list.isEmpty())
            indexKey.clear()
            indexKey.addAll(keys)
            indexKey.sort()
            fileMap.clear()
            fileMap.putAll(pictureDirMap)
            mAdapter.notifyDataSetChanged()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: Event) {
        if (event is Event.FileTaskSuccess || event is Event.FileDelete) {
            refresh(false)
        }
    }
}