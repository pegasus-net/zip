package com.icarus.unzip.ui.fragment

import a.icarus.component.BaseFragment
import android.view.View
import androidx.databinding.DataBindingUtil
import com.icarus.unzip.R
import com.icarus.unzip.databinding.FragmentFileNavigationBinding
import com.icarus.unzip.databinding.FragmentFileRecentBinding

class FileRecentFragment : BaseFragment() {
    private lateinit var binding: FragmentFileRecentBinding
    override fun setLayout(): Int {
        return R.layout.fragment_file_recent
    }

    override fun initView(view: View) {
        binding = DataBindingUtil.bind(view)!!
    }

    override fun initData() {

    }

    override fun initListener() {

    }
}