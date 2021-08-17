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
import com.icarus.unzip.util.toPxF
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class MainFragment : BaseFragment() {

    private lateinit var binding: FragmentMainBinding

    private var titles = listOf("最近", "文件")
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
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return titles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val titleView = ScaleTitleView(context)
                titleView.normalColor = 0xFF333333.toInt()
                titleView.selectedColor = 0xFF027AFF.toInt()
                titleView.setScale(0.9F)
                titleView.text = titles[index]
                titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17F)
                titleView.setOnClickListener { binding.pager.currentItem = index }
                return titleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.yOffset = 6.toPxF()
                indicator.lineWidth = 30.toPxF()
                indicator.lineHeight = 3.toPxF()
                indicator.roundRadius = 1.toPxF()
                indicator.setColors(0xFF027AFF.toInt())
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator()
                return indicator
            }
        }
        binding.indicator.navigator = commonNavigator
        ViewPagerHelper.bind(binding.indicator, binding.pager)
        binding.pager.currentItem = 1
    }

    override fun initListener() {
        binding.search.setOnClickListener {

        }
    }
}