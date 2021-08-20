package com.icarus.unzip.ui.activity

import a.icarus.impl.ColorFragment
import a.icarus.impl.FragmentAdapter
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.icarus.unzip.R
import com.icarus.unzip.ad.ExitAdDialog
import com.icarus.unzip.base.Activity
import com.icarus.unzip.ui.fragment.MainFragment
import com.icarus.unzip.ad.SetFragment
import com.icarus.unzip.data.Event
import com.icarus.unzip.databinding.ActivityMainBinding
import com.icarus.unzip.ui.fragment.UnzipHistoryFragment
import com.icarus.unzip.ui.fragment.ZipHistoryFragment
import com.icarus.unzip.util.BottomNavigationHelper
import com.icarus.unzip.util.getRootFile
import com.icarus.unzip.util.log
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : EditActivity() {

    private lateinit var binding: ActivityMainBinding
    private val fragments = listOf(
        MainFragment(), UnzipHistoryFragment(),
        ZipHistoryFragment(), SetFragment()
    )

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.bottom.itemIconTintList = null
    }

    override fun initData() {
        binding.pager.offscreenPageLimit = fragments.size
        binding.pager.adapter = FragmentAdapter(this, fragments)
        BottomNavigationHelper.bind(binding.pager, binding.bottom)
    }

    override fun initListener() {

    }

    override fun getZipPath(): String {
        return getRootFile().absolutePath
    }

    override fun getZipName(): String {
        return "压缩文件"
    }

    override fun onBackPressed() {
        if(editPlug.editMode){
            editPlug.editMode = false
            return
        }
        object : ExitAdDialog(this) {
            override fun show() {
                super.show()
                val ad = findViewById<ViewGroup>(R.id._native_express_ad_show)
            }
        }.setExitListener { finish() }.show()
    }
}