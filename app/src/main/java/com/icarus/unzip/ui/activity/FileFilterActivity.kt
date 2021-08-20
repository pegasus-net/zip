package com.icarus.unzip.ui.activity

import a.icarus.impl.FragmentAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.view.MotionEvent
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.icarus.unzip.R
import com.icarus.unzip.data.Event
import com.icarus.unzip.databinding.ActivityFileFilterBinding
import com.icarus.unzip.impl.FileNameCompare
import com.icarus.unzip.impl.FilterFactory
import com.icarus.unzip.impl.NavigatorAdapter
import com.icarus.unzip.ui.fragment.FileListFragment
import com.icarus.unzip.util.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class FileFilterActivity : EditActivity() {
    enum class Type {
        ZIP, MUSIC, TEXT, DOWNLOAD, QQ, WE_CHAT, BAI_DU
    }

    companion object {
        fun start(activity: Activity, type: Type) {
            val intent = Intent(activity, FileFilterActivity::class.java)
            intent.putExtra("type", type)
            activity.startActivity(intent)
        }


    }

    private lateinit var binding: ActivityFileFilterBinding
    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_file_filter)
        setBackView(R.id.back)
    }

    private var type = Type.ZIP
    override fun initData() {
        initEditPlug(binding.fileEditMenu)
        editPlug.editStateChangedListener = this::notifyDataSetChanged
        type = intent.getSerializableExtra("type") as Type? ?: Type.ZIP
        initDataByType()
        binding.pager.adapter = FragmentAdapter(supportFragmentManager, fragments)
        binding.pager.offscreenPageLimit = fragments.size - 1
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = NavigatorAdapter(titles) { index ->
            binding.pager.currentItem = index
        }
        binding.indicator.navigator = commonNavigator
        ViewPagerHelper.bind(binding.indicator, binding.pager)
        binding.refresh.setColorSchemeColors(getColor(R.color.main_color))
        refresh(0)
    }

    private val handler = Handler()
    private fun refresh(time: Long) {
        binding.refresh.isRefreshing = true
        subThread {
            val start = System.currentTimeMillis()
            loadData()
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

    override fun getZipPath(): String {
        return when (type) {
            Type.ZIP -> File(getRootFile(), "Zip").absolutePath
            Type.MUSIC -> File(getRootFile(), "Music").absolutePath
            Type.TEXT -> File(getRootFile(), "Zip").absolutePath
            Type.DOWNLOAD -> File(getRootFile(), "Download").absolutePath
            Type.QQ -> File(getRootFile(), "Zip").absolutePath
            Type.WE_CHAT -> File(getRootFile(), "Zip").absolutePath
            Type.BAI_DU -> File(getRootFile(), "Zip").absolutePath
        }
    }

    override fun getZipName(): String {
        return type.toString().toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
    }

    private lateinit var titles: Array<String>
    private lateinit var fragmentData: Array<ArrayList<File>>
    private lateinit var fragments: List<FileListFragment>
    private fun initDataByType() {
        titles = when (type) {
            Type.ZIP -> arrayOf("全部", "QQ", "微信", "百度网盘")
            Type.MUSIC -> arrayOf("全部", "QQ", "微信", "百度网盘", "系统录音")
            Type.TEXT -> arrayOf("全部", "Word", "Excel", "PPT", "PDF", "TXT")
            Type.DOWNLOAD -> arrayOf("全部", "压缩包", "视频", "图片", "音乐", "文档")
            Type.QQ -> arrayOf("全部", "压缩包", "视频", "图片", "音乐", "文档")
            Type.WE_CHAT -> arrayOf("全部", "压缩包", "视频", "图片", "音乐", "文档")
            Type.BAI_DU -> arrayOf("全部", "压缩包", "视频", "图片", "音乐", "文档")
        }
        binding.title.text = when (type) {
            Type.ZIP -> "压缩文件"
            Type.MUSIC -> "音乐"
            Type.TEXT -> "文档"
            Type.DOWNLOAD -> "下载中心"
            Type.QQ -> "QQ下载"
            Type.WE_CHAT -> "微信下载"
            Type.BAI_DU -> "百度下载"
        }
        fragmentData = Array(titles.size) { ArrayList() }
        fragments = fragmentData.map { FileListFragment(it, editPlug) }
    }

    private fun loadData() {
        val scanResult =FilterFactory.getScanResult(type)
        val fragmentTempData = Array(titles.size) {
            ArrayList<File>()
        }
        fragmentTempData.forEachIndexed { index, it ->
            it.addAll(scanResult.filter(FilterFactory.getFilterByName(titles[index])))
        }
        mainThread {
            fragmentData.forEachIndexed { index, arrayList ->
                arrayList.clear()
                arrayList.addAll(fragmentTempData[index])
            }
            notifyDataSetChanged()
        }
    }

    fun notifyDataSetChanged() {
        fragments.forEach { it.update() }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)
        if (event is Event.FileTaskSuccess || event is Event.FileDelete) {
            subThread { loadData() }
        }
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