package com.icarus.unzip.ui.fragment

import a.icarus.component.BaseFragment
import a.icarus.utils.FileUtil
import android.view.View
import androidx.databinding.DataBindingUtil
import com.icarus.unzip.R
import com.icarus.unzip.base.Activity
import com.icarus.unzip.databinding.FragmentFileNavigationBinding
import com.icarus.unzip.ui.activity.FileFilterActivity
import com.icarus.unzip.ui.activity.FileManagerActivity
import com.icarus.unzip.ui.activity.MediaManagerActivity
import com.icarus.unzip.util.startActivity


class FileNavigationFragment : BaseFragment() {
    private lateinit var binding: FragmentFileNavigationBinding
    override fun setLayout(): Int {
        return R.layout.fragment_file_navigation
    }

    override fun initView(view: View) {
        binding = DataBindingUtil.bind(view)!!
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        val availableBytes = FileUtil.getAvailableBytes()
        val totalBytes = FileUtil.getTotalBytes()
        val space = "已用${FileUtil.formatBytes(totalBytes - availableBytes)}" +
                "/共${FileUtil.formatBytes(totalBytes)}"
        binding.space.text = space
        binding.progressBar.progress = ((totalBytes - availableBytes) * 100 / totalBytes).toInt()
    }

    override fun initListener() {
        binding.sdcard.setOnClickListener {
            startActivity(FileManagerActivity::class.java)
        }
        binding.image.setOnClickListener {
            MediaManagerActivity.start(mActivity, MediaManagerActivity.MEDIA_IMAGE)
        }
        binding.video.setOnClickListener {
            MediaManagerActivity.start(mActivity, MediaManagerActivity.MEDIA_VIDEO)
        }
        binding.text.setOnClickListener {
            FileFilterActivity.start(mActivity, FileFilterActivity.Type.TEXT)
        }
        binding.zip.setOnClickListener {
            FileFilterActivity.start(mActivity, FileFilterActivity.Type.ZIP)
        }
        binding.music.setOnClickListener {
            FileFilterActivity.start(mActivity, FileFilterActivity.Type.MUSIC)
        }
        binding.download.setOnClickListener {
            FileFilterActivity.start(mActivity, FileFilterActivity.Type.DOWNLOAD)
        }
        binding.appQq.setOnClickListener {
            FileFilterActivity.start(mActivity, FileFilterActivity.Type.QQ)
        }
        binding.appVx.setOnClickListener {
            FileFilterActivity.start(mActivity, FileFilterActivity.Type.WE_CHAT)
        }
        binding.appBd.setOnClickListener {
            FileFilterActivity.start(mActivity, FileFilterActivity.Type.BAI_DU)
        }
    }
}