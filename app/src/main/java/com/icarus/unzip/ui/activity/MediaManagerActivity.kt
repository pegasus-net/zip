package com.icarus.unzip.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.util.TypedValue
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.icarus.unzip.R
import com.icarus.unzip.adapter.MediaAdapter
import com.icarus.unzip.coustomView.ScaleTitleView
import com.icarus.unzip.data.ExpandableTitle
import com.icarus.unzip.databinding.ActivityMediaBinding
import com.icarus.unzip.enums.FileType
import com.icarus.unzip.impl.NavigatorAdapter
import com.icarus.unzip.util.*
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MediaManagerActivity : EditActivity() {

    companion object {
        const val MEDIA_VIDEO = "video"
        const val MEDIA_IMAGE = "image"
        fun start(activity: Activity, mediaType: String) {
            val intent = Intent(activity, MediaManagerActivity::class.java)
            intent.putExtra(MEDIA_TYPE, mediaType)
            activity.startActivity(intent)
        }
    }

    private var titles = arrayOf("最近", "分类")
    private lateinit var binding: ActivityMediaBinding

    private lateinit var manager: GridLayoutManager
    private val indexKey = ArrayList<ExpandableTitle>()
    private val fileMap = HashMap<ExpandableTitle, ArrayList<File>>()
    private val helper = SecondaryListAdapterHelper(indexKey, fileMap, 3)
    private val mAdapter = MediaAdapter(helper, editPlug)
    private val scanResult = ArrayList<File>()
    private val handler = Handler()
    private var mediaType = ""
    private var currentPage = 0
        set(value) {
            if (field != value) {
                field = value
                subThread {
                    loadDataToAdapter()
                }
            }
        }

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media)
        setBackView(R.id.back)
    }

    override fun initData() {
        mediaType = intent.getStringExtra(MEDIA_TYPE) ?: ""
        binding.title.text = when (mediaType) {
            MEDIA_IMAGE -> "图片"
            MEDIA_VIDEO -> "视频"
            else -> mediaType
        }
        val containerHelper = FragmentContainerHelper(binding.indicator)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = NavigatorAdapter(titles) { index ->
            containerHelper.handlePageSelected(index)
            currentPage = index
        }
        binding.indicator.navigator = commonNavigator
        binding.mainList.adapter = mAdapter
        manager = GridLayoutManager(this, 3)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val any = helper.getItem(position)
                if (any is ExpandableTitle) return 3
                if (any is File) return 1
                return 0
            }
        }
        binding.mainList.layoutManager = manager
        initEditPlug(binding.fileEditMenu)
        editPlug.editStateChangedListener = { mAdapter.notifyDataSetChanged() }
        binding.refresh.setColorSchemeColors(getColor(R.color.main_color))
        refresh(700)
    }

    private fun refresh(time: Long) {
        binding.refresh.isRefreshing = true
        subThread {
            val start = System.currentTimeMillis()
            scanFile()
            loadDataToAdapter()
            val used = System.currentTimeMillis() - start
            handler.postDelayed(
                { binding.refresh.isRefreshing = false },
                (time - used).coerceAtLeast(0)
            )
        }
    }


    override fun initListener() {
        binding.menu.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            menuInflater.inflate(R.menu.popup_menu_1, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popup_0 -> setEditMode(true)
                    R.id.popup_1 -> if (!binding.refresh.isRefreshing) refresh(700) else "正在刷新".show()
                }
                true
            }
            popupMenu.show()
        }
        binding.refresh.setOnRefreshListener { refresh(0) }
    }


    override fun onBackPressed() {
        if (editPlug.editMode) {
            setEditMode(false)
            return
        }
        super.onBackPressed()
    }

    override fun getZipPath(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
    }

    override fun getZipName(): String {
        return "图片"
    }

    @Synchronized
    private fun scanFile() {
        scanResult.clear()
        val fileList = ArrayList<File>()
        getRootFile().scan(fileList)
        when (mediaType) {
            MEDIA_IMAGE -> scanResult.addAll(fileList.filter { it.getType() == FileType.IMAGE })
            MEDIA_VIDEO -> scanResult.addAll(fileList.filter { it.getType() == FileType.VIDEO })
        }
    }

    @Synchronized
    private fun loadDataToAdapter() {
        val list = scanResult.toList()
        val pictureDirMap = HashMap<ExpandableTitle, ArrayList<File>>()
        list.forEach {
            val expandableTitle = when (currentPage) {
                0 -> ExpandableTitle(it.getLastTime(), it.getLastTime().msg, it.getLastTime().id)
                else -> {
                    val parentFile = it.parentFile!!
                    ExpandableTitle(
                        parentFile,
                        parentFile.name,
                        parentFile.name.toLowerCase(Locale.ROOT)
                    )
                }
            }
            val index = expandableTitle.takeUnless { indexKey.contains(expandableTitle) }
                ?: indexKey[indexKey.indexOf(expandableTitle)]
            val data = pictureDirMap.getOrPut(index) { ArrayList() }
            data.add(it)
        }
        val keys = pictureDirMap.map { entry ->
            entry.value.sortBy {
                if (currentPage == 0) (Long.MAX_VALUE - it.lastModified()).toString()
                else it.name.toLowerCase(Locale.ROOT)
            }
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

    override fun delete(deleteFiles: List<File>) {
        super.delete(deleteFiles)
        scanResult.removeAll(deleteFiles)
        subThread {
            loadDataToAdapter()
        }
    }

    override fun afterOpenFileManager() {
        finish()
    }
}