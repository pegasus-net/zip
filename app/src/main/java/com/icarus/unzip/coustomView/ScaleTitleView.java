package com.icarus.unzip.coustomView;

import android.content.Context;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

public class ScaleTitleView extends ColorTransitionPagerTitleView {
    private float scale;

    public ScaleTitleView(Context context) {
        super(context);
        setScale(0.8f);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
        setScaleX(1.0f + (scale - 1.0f) * leavePercent);
        setScaleY(1.0f + (scale - 1.0f) * leavePercent);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
        setScaleX(scale + (1.0f - scale) * enterPercent);
        setScaleY(scale + (1.0f - scale) * enterPercent);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
