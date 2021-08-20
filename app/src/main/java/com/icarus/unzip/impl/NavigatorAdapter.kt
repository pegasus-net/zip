package com.icarus.unzip.impl

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.icarus.unzip.coustomView.ScaleTitleView
import com.icarus.unzip.util.toPxF
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

class NavigatorAdapter(
    private val titles: Array<String>,
    private val listener: (index: Int) -> Unit
) :
    CommonNavigatorAdapter() {
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
        titleView.setOnClickListener { listener(index)}
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