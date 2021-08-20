package com.icarus.unzip.ui.fragment

import a.icarus.component.BaseFragment
import a.icarus.impl.ColorFragment
import a.icarus.impl.FragmentAdapter
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.databinding.DataBindingUtil
import com.icarus.unzip.R
import com.icarus.unzip.coustomView.ScaleTitleView
import com.icarus.unzip.databinding.FragmentMainBinding
import com.icarus.unzip.impl.NavigatorAdapter
import com.icarus.unzip.ui.activity.SearchActivity
import com.icarus.unzip.util.toPxF
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class MainFragment : BaseFragment() {

    private lateinit var binding: FragmentMainBinding

    private var titles = arrayOf("最近", "文件")
    private val fragments = listOf(FileRecentFragment(), FileNavigationFragment())

    override fun setLayout(): Int {
        return R.layout.fragment_main
    }

    override fun initView(view: View) {
        binding = DataBindingUtil.bind(view)!!
    }

    override fun initData() {

        binding.pager.adapter = FragmentAdapter(childFragmentManager, fragments)

        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = NavigatorAdapter(titles) { index ->
            binding.pager.currentItem = index
        }
        binding.indicator.navigator = commonNavigator
        ViewPagerHelper.bind(binding.indicator, binding.pager)
        binding.pager.currentItem = 1
    }

    override fun initListener() {
        binding.search.setOnClickListener {
            SearchActivity.start(mActivity)
        }
    }
}