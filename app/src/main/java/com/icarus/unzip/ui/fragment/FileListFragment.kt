package com.icarus.unzip.ui.fragment

import a.icarus.component.BaseFragment
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.icarus.unzip.R
import com.icarus.unzip.adapter.AdapterEditPlug
import com.icarus.unzip.adapter.FileAdapter
import com.icarus.unzip.databinding.FragmentFileListBinding
import com.icarus.unzip.util.visible
import java.io.File

class FileListFragment(val list: List<File>, val editPlug: AdapterEditPlug) : BaseFragment() {
    private lateinit var binding: FragmentFileListBinding
    private val mAdapter = FileAdapter(list, editPlug)
    private var needUpdate = false
    override fun setLayout(): Int {
        return R.layout.fragment_file_list
    }

    override fun initView(view: View) {
        binding = DataBindingUtil.bind(view)!!
    }

    override fun initData() {
        binding.list.adapter = mAdapter
        binding.list.layoutManager = LinearLayoutManager(mActivity)
        if (needUpdate) update()
    }

    override fun initListener() {

    }

    fun update() {
        needUpdate = true
        if(this::binding.isInitialized){
            binding.empty.visible(list.isEmpty())
            needUpdate = false
        }
        mAdapter.notifyDataSetChanged()
    }
}